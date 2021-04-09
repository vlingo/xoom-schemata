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
import io.vlingo.xoom.schemata.model.Events;
import io.vlingo.xoom.schemata.query.view.*;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;

public class CodeProjection extends StateStoreProjectionActor<CodeView> {
	private String dataId;
	private final List<IdentifiedDomainEvent> events;

	public CodeProjection(StateStore stateStore) {
		super(stateStore);

		this.events = new ArrayList<>(2);
	}

	@Override
	protected CodeView currentDataFor(Projectable projectable) {
		return CodeView.with(projectable.dataId());
	}

	@Override
	protected String dataIdFor(Projectable projectable) {
		final Events.SchemaVersionDefined event = typed(events.get(0));
		dataId = dataIdFrom(event);
		return dataId;
	}

	@Override
	protected CodeView merge(CodeView previousData, int previousVersion, CodeView currentData, int currentVersion) {
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

	private String dataIdFrom(Events.SchemaVersionDefined event) {
		Completes<OrganizationView> organization = StorageProvider.instance().organizationQueries.organization(event.organizationId);
		Completes<UnitView> unit = StorageProvider.instance().unitQueries.unit(event.organizationId, event.unitId);
		Completes<ContextView> context = StorageProvider.instance().contextQueries.context(event.organizationId, event.unitId, event.contextId);
		Completes<SchemaView> schema = StorageProvider.instance().schemaQueries.schema(event.organizationId, event.unitId, event.contextId, event.schemaId);

		String result = dataIdFrom(Schemata.ReferenceSeparator,
				organization.<OrganizationView>await().name(),
				unit.<UnitView>await().name(),
				context.<ContextView>await().namespace(),
				schema.<SchemaView>await().name(),
				event.nextVersion);

		return result;
	}

	private CodeView mergeEventsInto(final CodeView initialData) {
		CodeView mergedData = initialData;
		for (final DomainEvent event : events) {
			switch (CodeViewType.match(event)) {
				case SchemaVersionDefined:
					final Events.SchemaVersionDefined defined = typed(event);
					final String reference = dataIdFrom(defined);
					mergedData = CodeView.with(reference, SchemaVersionView.with(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId,
							defined.schemaVersionId, defined.description, defined.specification, defined.status, defined.previousVersion, defined.nextVersion));
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
