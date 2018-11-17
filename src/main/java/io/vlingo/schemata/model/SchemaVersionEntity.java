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
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public final class SchemaVersionEntity extends EventSourced implements SchemaVersion {

  static {
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionDefined> applySchemaDefinedFn = SchemaVersionEntity::applyDefined;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionDefined.class, applySchemaDefinedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionSpecified> applySchemaVersionSpecifiedFn = SchemaVersionEntity::applySpecification;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionSpecified.class, applySchemaVersionSpecifiedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionDescribed> applySchemaDescribedFn = SchemaVersionEntity::applyDescribed;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionDescribed.class, applySchemaDescribedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionAssignedVersion> applySchemaVersionFn = SchemaVersionEntity::applyVersioned;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionAssignedVersion.class, applySchemaVersionFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionPublished> applySchemaVersionPublishedFn = SchemaVersionEntity::applyPublished;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionPublished.class, applySchemaVersionPublishedFn);
    BiConsumer<SchemaVersionEntity, Events.SchemaVersionRemoved> applySchemaVersionRemovedFn = SchemaVersionEntity::applyRemoved;
    EventSourced.registerConsumer(SchemaVersionEntity.class, Events.SchemaVersionRemoved.class, applySchemaVersionRemovedFn);
  }

  private State state;

  public SchemaVersionEntity(
          final SchemaVersionId schemaVersionId,
          final Category category,
          final String name,
          final String description,
          final Specification specification,
          final Version version) {
    assert (name != null && !name.isEmpty());
    assert (description != null && !description.isEmpty());
    apply(SchemaVersionDefined.with(schemaVersionId, category, name, description, specification, Status.Draft, version));
  }

  @Override
  public void assignVersionOf(final Version version) {
    assert (version != null);
    apply(new Events.SchemaVersionAssignedVersion(state.schemaVersionId, version));
  }

  @Override
  public void describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    assert (state.status.isDraft());
    apply(SchemaVersionDescribed.with(state.schemaVersionId, description));
  }

  @Override
  public void publish() {
    assert (state.status.isDraft());
    apply(SchemaVersionPublished.with(state.schemaVersionId));
  }

  @Override
  public void remove() {
    assert (state.status.isDraft());
    apply(SchemaVersionRemoved.with(state.schemaVersionId));
  }

  @Override
  public void specifyWith(final Specification specification) {
    assert (specification != null);
    apply(SchemaVersionSpecified.with(state.schemaVersionId, specification));
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

  private void applyPublished(final Events.SchemaVersionPublished e) {
    this.state = this.state.asPublished();
  }

  private void applyRemoved(final Events.SchemaVersionRemoved e) {
    this.state = this.state.asRemoved();
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

    public State asRemoved() {
      return new State(this.schemaVersionId, this.category, this.name, this.description, this.specification, Status.Removed, this.version);
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
  }

  @Override
  public TestState viewTestState() {
    TestState testState = new TestState();
    testState.putValue("sourced", this);
    return testState;
  }
}