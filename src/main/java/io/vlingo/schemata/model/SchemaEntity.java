// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Id.SchemaId;

public class SchemaEntity extends EventSourced implements Schema {
  private State state;

  public SchemaEntity(final SchemaId schemaId, final Category category, final String name, final String description) {
    apply(new SchemaDefined(schemaId, category, name, description));
  }

  @Override
  public void describeAs(String description) {
    apply(new SchemaDescribed(state.schemaId, description));
  }

  @Override
  public void recategorizedAs(final Category category) {
    apply(new SchemaRecategorized(state.schemaId, category));
  }

  @Override
  public void renameTo(String name) {
    apply(new SchemaRenamed(state.schemaId, name));
  }

  public class State {
    public final SchemaId schemaId;
    public final Category category;
    public final String description;
    public final String name;

    public State withCategory(final Category category) {
      return new State(this.schemaId, category, this.name, this.description);
    }

    public State withDescription(final String description) {
      return new State(this.schemaId, this.category, this.name, description);
    }

    public State withName(final String name) {
      return new State(this.schemaId, this.category, name, this.description);
    }

    public State(final SchemaId schemaId, final Category category, final String name, final String description) {
      this.schemaId = schemaId;
      this.category = category;
      this.name = name;
      this.description = description;
    }
  }

  @Override
  public TestState viewTestState() {
    TestState testState = new TestState();
    testState.putValue("sourced", this);
    return testState;
  }

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

  private void applyDefined(final SchemaDefined e) {
    state = new State(SchemaId.existing(e.schemaId), Category.valueOf(e.category), e.name, e.description);
  }

  private void applyDescribed(final SchemaDescribed e) {
    state = state.withDescription(e.description);
  }

  private void applyRecategorized(final SchemaRecategorized e) {
    state = state.withCategory(Category.valueOf(e.category));
  }

  private void applyRenamed(final SchemaRenamed event) {
    state = state.withName(event.name);
  }
}
