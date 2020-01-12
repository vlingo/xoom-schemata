// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDeprecated;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public final class SchemaVersionEntity  extends ObjectEntity<SchemaVersionState> implements SchemaVersion {
  private SchemaVersionState state;

  public SchemaVersionEntity(final SchemaVersionId schemaVersionId) {
    state = SchemaVersionState.from(schemaVersionId);
  }

  @Override
  public Completes<SchemaVersionState> defineWith(
          final Specification specification,
          final String description,
          final Version previousVersion,
          final Version nextVersion) {
    assert (description != null && !description.isEmpty());
    return apply(
        this.state.defineWith(description, specification, previousVersion, nextVersion),
        SchemaVersionDefined.with(state.schemaVersionId, specification, description, Status.Draft, previousVersion, nextVersion),
        () -> state);
  }

  @Override
  public Completes<SchemaVersionState> describeAs(final String description) {
    if (description != null && !description.isEmpty()) {
      return apply(this.state.withDescription(description), SchemaVersionDescribed.with(state.schemaVersionId, description), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> publish() {
    if (state.status.isDraft()) {
      return apply(this.state.asPublished(), SchemaVersionPublished.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> deprecate() {
    if (state.status.isPublished()) {
      return apply(this.state.asDeprecated(), SchemaVersionDeprecated.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> remove() {
    if (state.status.isDeprecated()) {
      return apply(this.state.asRemoved(), SchemaVersionRemoved.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> specifyWith(final Specification specification) {
    if (specification != null) {
      return apply(this.state.withSpecification(specification), SchemaVersionSpecified.with(state.schemaVersionId, specification), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  protected String id() {
    return String.valueOf(state.persistenceId());
  }

  @Override
  protected SchemaVersionState stateObject() {
    return state;
  }

  @Override
  protected void stateObject(final SchemaVersionState stateObject) {
    this.state = stateObject;
  }

  @Override
  protected Class<SchemaVersionState> stateObjectType() {
    return SchemaVersionState.class;
  }
}
