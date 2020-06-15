// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Events;
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.query.view.*;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.List;

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
			dataId = dataIdFromIds(defined.organizationId, defined.unitId, defined.contextId, defined.schemaId);
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

	private String dataIdFromIds(String organizationId, String unitId, String contextId, String schemaId) {
		Completes<OrganizationView> organization = StorageProvider.instance().organizationQueries.organization(organizationId);
		Completes<UnitView> unit = StorageProvider.instance().unitQueries.unit(organizationId, unitId);
		Completes<ContextView> context = StorageProvider.instance().contextQueries.context(organizationId, unitId, contextId);
		Completes<SchemaView> schema = StorageProvider.instance().schemaQueries.schema(organizationId, unitId, contextId, schemaId);

		String result = dataIdFrom(Schemata.ReferenceSeparator,
				organization.<OrganizationView>await().name(),
				unit.<UnitView>await().name(),
				context.<ContextView>await().namespace(),
				schema.<SchemaView>await().name());

		return result;
	}

	private NamedSchemaView mergeEventsInto(final NamedSchemaView initialData) {
		NamedSchemaView mergedData = initialData;
		for (final DomainEvent event : events) {
			switch(NamedSchemaViewType.match(event)) {
				case SchemaDefined:
					Events.SchemaDefined schemaDefined = typed(event);
					final String reference = dataIdFromIds(schemaDefined.organizationId, schemaDefined.unitId, schemaDefined.contextId, schemaDefined.schemaId);
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
