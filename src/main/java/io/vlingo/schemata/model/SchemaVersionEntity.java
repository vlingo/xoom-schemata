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
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Events.SchemaVersionStatusChanged;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public final class SchemaVersionEntity extends EventSourced implements SchemaVersion {

  static {
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionDefined> applySchemaDefinedFn = SchemaVersionEntity::applyDefined;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionDefined.class, applySchemaDefinedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionSpecified> applySchemaVersionSpecifiedFn = SchemaVersionEntity::applySpecification;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionSpecified.class, applySchemaVersionSpecifiedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionDescribed> applySchemaDescribedFn = SchemaVersionEntity::applyDescribed;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionDescribed.class, applySchemaDescribedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionStatusChanged> applySchemaVersionStatusFn = SchemaVersionEntity::applyStatus;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionStatusChanged.class, applySchemaVersionStatusFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionAssignedVersion> applySchemaVersionFn = SchemaVersionEntity::applyVersioned;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionAssignedVersion.class, applySchemaVersionFn);
  }

  private State state;

  public SchemaVersionEntity(
          final SchemaVersionId schemaVersionId,
          final Category category,
          final String name,
          final String description,
          final Specification specification,
          final Status status,
          final Version version) {
    assert (name != null && !name.isEmpty());
    assert (description != null && !description.isEmpty());
    apply(SchemaVersionDefined.with(schemaVersionId, category, name, description, specification, status, version));
  }

  @Override
  public void describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    apply(SchemaVersionDescribed.with(state.schemaVersionId, description));
  }

  @Override
  public void assignStatus(final Status status) {
    assert (status != null);
    apply(SchemaVersionStatusChanged.with(state.schemaVersionId, status));
  }

  @Override
  public void specifiedAs(final Specification specification) {
    assert (specification != null);
    apply(SchemaVersionSpecified.with(state.schemaVersionId, specification));

  }

  @Override
  public void assignVersion(Version version) {
    assert (version != null);
    apply(new Events.SchemaVersionAssignedVersion(state.schemaVersionId, version));
  }

  private void applyDefined(final Events.SchemaVersionDefined e) {
    this.state = new State(SchemaVersionId.existing(e.schemaVersionId), Category.valueOf(e.category), e.name, e.description,
            new Specification(e.specification), Status.valueOf(e.status), new Version(e.version));
  }

  private void applySpecification(final Events.SchemaVersionSpecified e) {
    this.state = this.state.withSpecification(new Specification(e.specification));
  }

  private void applyDescribed(final Events.SchemaVersionDescribed e) {
    this.state = this.state.withDescription(e.description);
  }

  private void applyVersioned(final Events.SchemaVersionAssignedVersion e) {
    this.state = this.state.withVersion(new Version(e.version));
  }

  private void applyStatus(final Events.SchemaVersionStatusChanged e) {
    this.state = this.state.withStatus(Status.valueOf(e.status));
  }

  public class State {
    public final SchemaVersionId schemaVersionId;
    public final Category category;
    public final String name;
    public final String description;
    public final Specification specification;
    public final Status status;
    public final Version version;

    public State(
            final SchemaVersionId schemaVersionId,
            final Category category,
            final String name,
            final String description,
            final Specification specification,
            final Status status,
            final Version version) {
      this.schemaVersionId = schemaVersionId;
      this.category = category;
      this.name = name;
      this.description = description;
      this.specification = specification;
      this.status = status;
      this.version = version;
    }

    public State asPublished() {
      return new State(this.schemaVersionId, this.category, this.name, this.description, this.specification, Status.Published, this.version);
    }

    public State withSpecification(final Specification specification) {
      return new State(this.schemaVersionId, this.category, this.name, this.description, specification, this.status, this.version);
    }

    public State withDescription(final String description) {
      return new State(this.schemaVersionId, this.category, this.name, description, this.specification, this.status, this.version);
    }

    public State withVersion(final Version version) {
      return new State(this.schemaVersionId, this.category, this.name, this.description, this.specification, this.status, version);
    }

    public State withStatus(final Status status) {
      return new State(this.schemaVersionId, this.category, this.name, this.description, this.specification, status, this.version);
    }
  }

  @Override
  public TestState viewTestState() {
    TestState testState = new TestState();
    testState.putValue("sourced", this);
    return testState;
  }
}