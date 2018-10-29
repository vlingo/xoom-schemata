// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;

public class SchemaEntity extends EventSourced implements Schema {

  static {
    BiConsumer<SchemaEntity, SchemaDefined> applySchemaDefinedFn = SchemaEntity::applyDefined;
    EventSourced.registerConsumer(SchemaEntity.class, SchemaDefined.class, applySchemaDefinedFn);
    BiConsumer<SchemaEntity, SchemaRecategorized> applySchemaRecategorizedFn = SchemaEntity::applyRecategorized;
    EventSourced.registerConsumer(SchemaEntity.class, SchemaRecategorized.class, applySchemaRecategorizedFn);
    BiConsumer<SchemaEntity, SchemaDescribed> applySchemaDescribedFn = SchemaEntity::applyDescribed;
    EventSourced.registerConsumer(SchemaEntity.class, SchemaDescribed.class, applySchemaDescribedFn);
    BiConsumer<SchemaEntity, SchemaRenamed> applySchemaRenamedFn = SchemaEntity::applyRenamed;
    EventSourced.registerConsumer(SchemaEntity.class, SchemaRenamed.class, applySchemaRenamedFn);
  }

  private State state;

  public SchemaEntity(
          final OrganizationId organizationId,
          final UnitId unitId,
          final ContextId contextId,
          final SchemaId schemaId,
          final Category category,
          final String name,
          final String description) {
    apply(new SchemaDefined(organizationId, unitId, contextId, schemaId, category, name, description));
  }

  @Override
  public void describeAs(String description) {
    apply(new SchemaDescribed(state.organizationId, state.unitId, state.contextId, state.schemaId, description));
  }

  @Override
  public void recategorizedAs(final Category category) {
    apply(new SchemaRecategorized(state.organizationId, state.unitId, state.contextId, state.schemaId, category));
  }

  @Override
  public void renameTo(String name) {
    apply(new SchemaRenamed(state.organizationId, state.unitId, state.contextId, state.schemaId, name));
  }

  public void applyDefined(SchemaDefined e) {
    state = new State(OrganizationId.existing(e.organizationId), UnitId.existing(e.unitId),
            ContextId.existing(e.contextId), SchemaId.existing(e.schemaId),
            Category.valueOf(e.category), e.name, e.description);
  }

  public void applyDescribed(SchemaDescribed e) {
    state = state.withDescription(e.description);
  }

  public void applyRecategorized(SchemaRecategorized e) {
    state = state.withCategory(Category.valueOf(e.category));
  }

  public void applyRenamed(SchemaRenamed event) {
    state = state.withName(event.name);
  }

  public class State {
    public final OrganizationId organizationId;
    public final UnitId unitId;
    public final ContextId contextId;
    public final SchemaId schemaId;
    public final Category category;
    public final String description;
    public final String name;

    public State(
            final OrganizationId organizationId,
            final UnitId unitId,
            final ContextId contextId,
            final SchemaId schemaId,
            final Category category,
            final String name,
            final String description) {
      this.organizationId = organizationId;
      this.unitId = unitId;
      this.contextId = contextId;
      this.schemaId = schemaId;
      this.category = category;
      this.name = name;
      this.description = description;
    }

    public State withCategory(final Category category) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, category, this.name, this.description);
    }

    public State withDescription(final String description) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.category, this.name, description);
    }

    public State withName(final String name) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.category, name, this.description);
    }
  }
}
