// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Collections;
import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.SchemaVersionAssignedVersion;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.symbio.Source;

public final class SchemaVersionEntity  extends ObjectEntity<SchemaVersionState> implements SchemaVersion {
  private SchemaVersionState state;

  public SchemaVersionEntity(final SchemaVersionId schemaVersionId) {
    state = SchemaVersionState.from(schemaVersionId);
  }

  @Override
  public Completes<SchemaVersionState> defineWith(
          final String description,
          final Specification specification,
          final Version version) {
    assert (description != null && !description.isEmpty());
    apply(
        this.state.defineWith(description, specification, version),
        SchemaVersionDefined.with(state.schemaVersionId, description, specification, Status.Draft, version),
        () -> state);
    return completes();
  }

  @Override
  public Completes<SchemaVersionState> assignVersionOf(final Version version) {
    assert (version != null);
    apply(this.state.withVersion(version), SchemaVersionAssignedVersion.with(state.schemaVersionId, version), () -> this.state);
    return completes();
  }

  @Override
  public Completes<SchemaVersionState> describeAs(final String description) {
    assert (description != null && !description.isEmpty());
    assert (state.status.isDraft());
    apply(this.state.withDescription(description), SchemaVersionDescribed.with(state.schemaVersionId, description), () -> this.state);
    return completes();
  }

  @Override
  public Completes<SchemaVersionState> publish() {
    assert (state.status.isDraft());
    apply(this.state.asPublished(), SchemaVersionPublished.with(state.schemaVersionId), () -> this.state);
    return completes();
  }

  @Override
  public Completes<SchemaVersionState> remove() {
    assert (state.status.isDraft());
    apply(this.state.asRemoved(), SchemaVersionRemoved.with(state.schemaVersionId), () -> this.state);
    return completes();
  }

  @Override
  public Completes<SchemaVersionState> specifyWith(final Specification specification) {
    assert (specification != null);
    apply(this.state.withSpecification(specification), SchemaVersionSpecified.with(state.schemaVersionId, specification), () -> this.state);
    return completes();
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Tuple2<SchemaVersionState, List<Source<DomainEvent>>> whenNewState() {
    return Tuple2.from(this.state, Collections.emptyList());
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected void persistentObject(final SchemaVersionState persistentObject) {
    this.state = persistentObject;
  }

  @Override
  protected Class<SchemaVersionState> persistentObjectType() {
    return SchemaVersionState.class;
  }
}
