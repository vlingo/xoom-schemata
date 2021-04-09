// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Events;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.query.view.*;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NamedSchemaProjection extends StateStoreProjectionActor<NamedSchemaView> {
	private String dataId;
	private final List<IdentifiedDomainEvent> events;

	public NamedSchemaProjection(StateStore stateStore) {
		super(stateStore);

		this.events = new ArrayList<>(2);
	}

	@Override
	protected NamedSchemaView currentDataFor(Projectable projectable) {
		return NamedSchemaView.empty();
	}

	@Override
	protected String dataIdFor(Projectable projectable) {
		IdentifiedDomainEvent event = events.get(0);
		if (event instanceof Events.SchemaDefined) {
			Events.SchemaDefined defined = typed(events.get(0));
			dataId = dataIdFromIdsAndName(defined.organizationId, defined.unitId, defined.contextId, defined.name);
		} else if (event instanceof Events.SchemaVersionDefined) {
			Events.SchemaVersionDefined defined = typed(events.get(0));
			dataId = dataIdFromIds(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId);
		} else {
			throw new UnsupportedOperationException("Unsupported event type: " + event.getClass().getSimpleName() + "!");
		}

		return dataId;
	}

	@Override
	protected boolean alwaysWrite() {
		return false;
	}

	@Override
	protected NamedSchemaView merge(NamedSchemaView previousData, int previousVersion, NamedSchemaView currentData, int currentVersion) {
		return previousData == null
				? mergeEventsInto(currentData)
				: mergeEventsInto(previousData);
	}

	@Override
	protected void prepareForMergeWith(Projectable projectable) {
		events.clear();

		for (final Entry<?> entry : projectable.entries()) {
			events.add(entryAdapter().anyTypeFromEntry(entry));
		}
	}

	private OrganizationView organizationView(String organizationId) {
		Completes<OrganizationView> organizationView = StorageProvider.instance().organizationQueries.organization(organizationId);
		return Optional.ofNullable(organizationView.<OrganizationView>await())
				.orElse(OrganizationView.empty());
	}

	private UnitView unitView(String organizationId, String unitId) {
		Completes<UnitView> unitView = StorageProvider.instance().unitQueries.unit(organizationId, unitId);
		return Optional.ofNullable(unitView.<UnitView>await())
				.orElse(UnitView.empty());
	}

	private ContextView contextView(String organizationId, String unitId, String contextId) {
		Completes<ContextView> contextView = StorageProvider.instance().contextQueries.context(organizationId, unitId, contextId);
		return Optional.ofNullable(contextView.<ContextView>await())
				.orElse(ContextView.empty());
	}

	private SchemaView schemaView(String organizationId, String unitId, String contextId, String schemaId) {
		Completes<SchemaView> schemaView = StorageProvider.instance().schemaQueries.schema(organizationId, unitId, contextId, schemaId);
		return Optional.ofNullable(schemaView.<SchemaView>await())
				.orElse(SchemaView.empty());
	}

	private String dataIdFromIds(String organizationId, String unitId, String contextId, String schemaId) {
		String organization = organizationView(organizationId).name();
		String unit = unitView(organizationId, unitId).name();
		String context = contextView(organizationId, unitId, contextId).namespace();
		String schema = schemaView(organizationId, unitId, contextId, schemaId).name();

		String result = dataIdFrom(Schemata.ReferenceSeparator, organization, unit, context, schema);

		return result;
	}

	private String dataIdFromIdsAndName(String organizationId, String unitId, String contextId, String schema) {
		String organization = organizationView(organizationId).name();
		String unit = unitView(organizationId, unitId).name();
		String context = contextView(organizationId, unitId, contextId).namespace();

		String result = dataIdFrom(Schemata.ReferenceSeparator, organization, unit, context, schema);

		return result;
	}

	private NamedSchemaView mergeEventsInto(final NamedSchemaView initialData) {
		NamedSchemaView mergedData = initialData;
		for (final DomainEvent event : events) {
			switch(NamedSchemaViewType.match(event)) {
				case SchemaDefined:
					Events.SchemaDefined schemaDefined = typed(event);
					final String reference = dataIdFromIdsAndName(schemaDefined.organizationId, schemaDefined.unitId, schemaDefined.contextId, schemaDefined.name);
					mergedData = NamedSchemaView.with(reference, SchemaView.with(schemaDefined.organizationId, schemaDefined.unitId, schemaDefined.contextId,
							schemaDefined.schemaId, Category.valueOf(schemaDefined.category), Scope.valueOf(schemaDefined.scope), schemaDefined.name,
							schemaDefined.description));
					break;
				case SchemaVersionDefined:
					Events.SchemaVersionDefined schemaVersionDefined = typed(event);
					SchemaVersionView schemaVersion = SchemaVersionView.with(schemaVersionDefined.organizationId, schemaVersionDefined.unitId,
							schemaVersionDefined.contextId, schemaVersionDefined.schemaId, schemaVersionDefined.schemaVersionId,
							schemaVersionDefined.description, schemaVersionDefined.specification, schemaVersionDefined.status,
							schemaVersionDefined.previousVersion, schemaVersionDefined.nextVersion);
					mergedData = mergedData.addSchemaVersion(schemaVersion);
					break;
				case Unmatched:
					logger().warn("Event of type " + event.typeName() + " was not matched.");
					break;
			}
		}

		logger().info("PROJECTED: " + mergedData);

		return mergedData;
	}
}
