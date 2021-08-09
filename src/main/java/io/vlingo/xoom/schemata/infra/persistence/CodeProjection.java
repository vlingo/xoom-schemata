// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.Projection;
import io.vlingo.xoom.lattice.model.projection.ProjectionControl;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.schemata.infra.persistence.SchemaVersionLookUp.SchemaVersionLookUpInstantiator;
import io.vlingo.xoom.schemata.model.Events;
import io.vlingo.xoom.schemata.query.view.CodeView;
import io.vlingo.xoom.schemata.query.view.ContextView;
import io.vlingo.xoom.schemata.query.view.OrganizationView;
import io.vlingo.xoom.schemata.query.view.PathBuilder;
import io.vlingo.xoom.schemata.query.view.SchemaVersionView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.schemata.query.view.UnitView;
import io.vlingo.xoom.schemata.query.view.View;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class CodeProjection extends StateStoreProjectionActor<CodeView> implements SchemaVersionLookUpInterest {
	private ScratchPad currentScratchPad;
	private final Projection projection;
	private final SchemaVersionLookUpInterest lookUpInterest;
  private final Map<String, ScratchPad> scratchPads;

  public CodeProjection(StateStore stateStore) {
		super(stateStore);

		this.scratchPads = new ConcurrentHashMap<>();

		this.projection = selfAs(Projection.class);

		this.lookUpInterest = selfAs(SchemaVersionLookUpInterest.class);
	}

  @Override
  public void informFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view, final String value) {
    informFound(projectable, projectionControl, view, value, "");
  }

  @Override
  public void informFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view, final String value1, final String value2) {
    final String projectionId = projectable.projectionId();

    final ScratchPad scratchPad = scratchPads.get(projectionId);

    if (scratchPad != null) {
      if (view == OrganizationView.class) {
        scratchPad.appendOrganizationSegment(value1);
      } else if (view == UnitView.class) {
        scratchPad.appendUnitSegment(value1);
      } else if (view == ContextView.class) {
        scratchPad.appendContextSegment(value1);
      } else if (view == SchemaView.class) {
        scratchPad.appendSchemaVersionSegment(value1, value2);
      }

      logger().debug("INFORM: " + projectionId + " VALUE1: " + value1 + " VALUE2: " + value2 + " PATH: " + scratchPad.pathReference());

      if (scratchPad.isCompleted()) {
        logger().debug("INFORM COMPLETED: " + projectionId);
        projection.projectWith(projectable, projectionControl);
      }
    }
  }

  @Override
  public void informNotFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view) {
    final String projectionId = projectable.projectionId();

    logger().debug("NOT-FOUND: " + projectionId);

    final ScratchPad scratchPad = scratchPads.get(projectionId);

    if (scratchPad != null) {
      if (!scratchPad.hasExhaustedRetries()) {
        scratchPad.retryLookUps();
      } else {
        logger().debug("NOT-FOUND: " + projectionId + " DEFUNCT");
        scratchPads.remove(projectionId);
      }
    }
  }

  @Override
  protected boolean readyForUpsert(final Projectable projectable, final ProjectionControl control) {
	  final ScratchPad scratchPad = scratchPadFor(projectable);

	  if (scratchPad != null) {
	    final String projectionId = scratchPad.projectable.projectionId();

	    final boolean ready = scratchPad.isCompleted();

      logger().debug("READY: " + projectionId + " COMPLETED: " + ready);

      return ready;
	  }

    logger().debug("READY: " + projectable.projectionId() + " STARTING");

	  startLookUps(projectable, control);

	  return false;
  }

	@Override
	protected CodeView currentDataFor(Projectable projectable) {
		return CodeView.with(projectable.dataId());
	}

	@Override
	protected String dataIdFor(final Projectable projectable) {
    final ScratchPad scratchPad = scratchPadFor(projectable);
		return scratchPad.pathReference();
	}

	@Override
	protected CodeView merge(CodeView previousData, int previousVersion, CodeView currentData, int currentVersion) {
		return previousData == null
				? mergeEventsInto(currentData)
				: mergeEventsInto(previousData);
	}

	@Override
	protected void prepareForMergeWith(final Projectable projectable) {
	  currentScratchPad = scratchPadFor(projectable);
	}

	private List<IdentifiedDomainEvent> eventsFrom(final Projectable projectable) {
	  final List<IdentifiedDomainEvent> events = new ArrayList<>(1);

	  for (final Entry<?> entry : projectable.entries()) {
	    final IdentifiedDomainEvent event = entryAdapter().anyTypeFromEntry(entry);

	    events.add(event);
	  }

    return Collections.unmodifiableList(events);
	}

	private CodeView mergeEventsInto(final CodeView initialData) {
		CodeView mergedData = initialData;
		for (final DomainEvent event : currentScratchPad.events) {
			switch (CodeViewType.match(event)) {
				case SchemaVersionDefined: {
					final Events.SchemaVersionDefined defined = typed(event);

					final String reference = currentScratchPad.pathReference();

					mergedData = CodeView.with(reference, SchemaVersionView.with(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId,
							defined.schemaVersionId, defined.description, defined.specification, defined.status, defined.previousVersion, defined.nextVersion));

					scratchPads.remove(currentScratchPad.projectable.projectionId());

			    logger().debug("MERGED AND REMOVED: " + currentScratchPad.projectable.projectionId());

          break;
			    }
				case Unmatched:
					logger().warn("Event of type " + event.typeName() + " was not matched.");
					break;
			}
		}

		logger().info("PROJECTED: " + currentScratchPad.events.stream().map(Source::typeName).collect(Collectors.toList()) + ", " + mergedData);

		return mergedData;
	}

  private ScratchPad scratchPadFor(final Projectable projectable) {
    final ScratchPad scratchPad = scratchPads.get(projectable.projectionId());

    return scratchPad;
  }

  private void startLookUps(final Projectable projectable, final ProjectionControl control) {
    final List<IdentifiedDomainEvent> events = eventsFrom(projectable);

    final Events.SchemaVersionDefined event = (Events.SchemaVersionDefined) events.get(0);

    final SchemaVersionLookUpInstantiator instantiator =
            new SchemaVersionLookUpInstantiator(lookUpInterest, event.nextVersion, projectable, control);

    final SchemaVersionLookUp lookUp =
            stage().actorFor(SchemaVersionLookUp.class, SchemaVersionLookUpActor.class, instantiator);

    lookUp.lookUpOrganizationName(OrganizationView.class, () -> StorageProvider.instance().organizationQueries.organization(event.organizationId));

    lookUp.lookUpUnitName(UnitView.class, () -> StorageProvider.instance().unitQueries.unit(event.organizationId, event.unitId));

    lookUp.lookUpContextNamespace(ContextView.class, () -> StorageProvider.instance().contextQueries.context(event.organizationId, event.unitId, event.contextId));

    lookUp.lookUpSchemaName(SchemaView.class, () -> StorageProvider.instance().schemaQueries.schema(event.organizationId, event.unitId, event.contextId, event.schemaId));

    scratchPads.put(projectable.projectionId(), new ScratchPad(projectable, lookUp, events, logger()));
  }

  private static final class ScratchPad {
    static final int Resolved = -1;
    static final int TotalTries = 10;

    final List<IdentifiedDomainEvent> events;
    final Logger logger;
    final Projectable projectable;
    final SchemaVersionLookUp schemaVersionLookUp;

    private PathBuilder pathBuilder;
    private int remainingTries;

    ScratchPad(final Projectable projectable, final SchemaVersionLookUp schemaVersionLookUp, final List<IdentifiedDomainEvent> events, final Logger logger) {
      this.projectable = projectable;
      this.schemaVersionLookUp = schemaVersionLookUp;
      this.events = events;
      this.logger = logger;
      this.pathBuilder = PathBuilder.instance();
      this.remainingTries = TotalTries;
    }

    void appendOrganizationSegment(final String organizationName) {
      pathBuilder = pathBuilder.withOrganization(organizationName);
      determineCompleted();
    }

    void appendUnitSegment(final String unitName) {
      pathBuilder = pathBuilder.withUnit(unitName);
      determineCompleted();
    }

    void appendContextSegment(final String contextNamespace) {
      pathBuilder = pathBuilder.withContext(contextNamespace);
      determineCompleted();
    }

    void appendSchemaVersionSegment(final String schemaName, final String version) {
      pathBuilder = pathBuilder.withSchema(schemaName).withVersion(version);
      determineCompleted();
    }

    boolean isCompleted() {
      logger.debug("IS-COMPLETED: " + " FULL PATH: " + pathBuilder.isFullyDefined());

      return pathBuilder.isFullyDefined();
    }

    boolean hasExhaustedRetries() {
      return remainingTries == 0;
    }

    String pathReference() {
      return pathBuilder.toString();
    }

    void retryLookUps() {
      if (remainingTries > 0) {
        schemaVersionLookUp.retryLookUps();
        --remainingTries;
      }
    }

    private void determineCompleted() {
      if (isCompleted()) {
        remainingTries = Resolved;
      }
    }
  }
}
