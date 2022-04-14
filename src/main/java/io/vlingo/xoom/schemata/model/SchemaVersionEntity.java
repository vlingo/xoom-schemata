// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.common.*;
import io.vlingo.xoom.lattice.model.sourcing.EventSourced;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.xoom.schemata.codegen.ast.FieldDefinition;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import io.vlingo.xoom.schemata.model.Events.*;
import io.vlingo.xoom.schemata.model.Id.SchemaVersionId;
import io.vlingo.xoom.schemata.resource.data.SchemaVersionData;

public final class SchemaVersionEntity extends EventSourced implements SchemaVersion {
  private SchemaVersionState state;

  public SchemaVersionEntity(final SchemaVersionId schemaVersionId) {
    super(schemaVersionId.value);
    state = SchemaVersionState.from(schemaVersionId);
  }

  @Override
  public Completes<SchemaVersionState> defineWith(
          final Specification specification,
          final String description,
          final Version previousVersion,
          final Version nextVersion) {
    assert (description != null && !description.isEmpty());
    return apply(SchemaVersionDefined.with(this.state.schemaVersionId, specification, description, Status.Draft, previousVersion, nextVersion),
            () -> state);
  }

  @Override
  public Completes<SchemaVersionState> describeAs(final String description) {
    if (description != null && !description.isEmpty()) {
      return apply(SchemaVersionDescribed.with(state.schemaVersionId, description), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> publish() {
    if (state.status.isDraft()) {
      return apply(SchemaVersionPublished.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> deprecate() {
    if (state.status.isPublished()) {
      return apply(SchemaVersionDeprecated.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> remove() {
    if (state.status.isDeprecated()) {
      return apply(SchemaVersionRemoved.with(state.schemaVersionId), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<SchemaVersionState> specifyWith(final Specification specification) {
    if (specification != null && state.status.isDraft()) {
      return apply(SchemaVersionSpecified.with(state.schemaVersionId, specification), () -> this.state);
    }
    return completes().with(state);
  }

  @Override
  public Completes<Outcome<SchemataBusinessException, SpecificationDiff>> diff(final TypeDefinitionMiddleware typeDefinitionMiddleware, final SchemaVersionData other) {
    final CompletesEventually completesEventually = completesEventually();

    if (other.specification == null || other.specification.isEmpty()) {
      completesEventually.with(Failure.of(SchemataBusinessException.invalidSchemaDefinition()));
      return completes();
    }

    SpecificationDiff diff = SpecificationDiff.between(state.specification.value, other.specification);

    // FIXME: Make reactive, don't await() the node
    Outcome<SchemataBusinessException,Node> leftAst = typeDefinitionMiddleware.compileToAST(
            new ByteArrayInputStream(state.specification.value.getBytes(StandardCharsets.UTF_8)),
            null).await();

    if(leftAst instanceof Failure) {
      completesEventually.with(((Failure<SchemataBusinessException, Node>) leftAst).cause());
      return completes();
    }

    TypeDefinition leftType = asTypeDefinition(leftAst.get());

    Outcome<SchemataBusinessException,Node> rightAst = typeDefinitionMiddleware.compileToAST(
            new ByteArrayInputStream(other.specification.getBytes(StandardCharsets.UTF_8)),
            null).await();

    if(rightAst instanceof Failure) {
      completesEventually.with(((Failure<SchemataBusinessException, Node>) rightAst).cause());
      return completes();
    }

    TypeDefinition rightType = asTypeDefinition(rightAst.get());


    // Has the type name changed?
    if (!leftType.typeName.equals(rightType.typeName))
      diff = diff.withChange(Change.ofType(leftType.typeName, rightType.typeName));

    // Collect changed fields
    List<Tuple2<FieldDefinition,FieldDefinition>> changedFields = Collections.synchronizedList(new ArrayList<>());
    @SuppressWarnings("unused")
    List<Node> changes = leftType.children.stream()
      .map(SchemaVersionEntity::asFieldDefinition)
      .filter(l -> rightType.children.stream()
        .map(SchemaVersionEntity::asFieldDefinition)
        .anyMatch(r -> {
            boolean changed = l.name().equals(r.name())
              && (
              !l.type.equals(r.type)
                || !l.version.equals(r.version)
                || !l.defaultValue.equals(r.defaultValue)
            );
            if(changed) {
              changedFields.add(Tuple2.from(l,r));
            }
            return changed;
          }
        )
      )
      .collect(toList());

    // Collect removed fields
    List<FieldDefinition> removals = leftType.children.stream()
      .filter(l -> rightType.children.stream().noneMatch(r -> l.name().equals(r.name())))
      .map(SchemaVersionEntity::asFieldDefinition)
      .collect(toList());

    // Collect added fields
    List<FieldDefinition> additions = rightType.children.stream()
      .filter(r -> leftType.children.stream().noneMatch(l -> l.name().equals(r.name())))
      .map(SchemaVersionEntity::asFieldDefinition)
      .collect(toList());


    // record changes
    for (Tuple2<FieldDefinition,FieldDefinition> change: changedFields) {
      if(!change._1.version.equals(change._2.version)) {
        diff = diff.withChange(Change.ofVersion(change._1.name(), change._1.version.toString(), change._2.version.toString()));
      }
      if(!change._1.type.equals(change._2.type)) {
        diff = diff.withChange(Change.ofFieldType(change._1.name(), change._1.type.name(), change._2.type.name()));
      }
    }

    // record additions
    for(FieldDefinition addition : additions) {
      diff = diff.withChange(Change.additionOfField(addition.name));
    }

    // record deletions
    for(FieldDefinition removal : removals) {
      diff = diff.withChange(Change.removalOfField(removal.name));
    }

    //record moves
    for(int i = 0; i < rightType.children.size(); i++) {
      Node right = rightType.children.get(i);
      if(i < leftType.children.size()
          && leftType.children.indexOf(right) != -1
          && !right.name().equals(leftType.children.get(i).name())) {
            diff = diff.withChange(
              Change.moveOf(right.name()));
      }
    }

    completesEventually.with(Success.of(diff));
    return completes();
  }

  //==============================
  // Internal implementation
  //==============================

  private static TypeDefinition asTypeDefinition(Node n) {
    return Processor.requireBeing(n, TypeDefinition.class);
  }

  private static FieldDefinition asFieldDefinition(Node n) {
    return Processor.requireBeing(n, FieldDefinition.class);
  }

  private void applySchemaVersionDefined(SchemaVersionDefined event) {
    this.state = state.defineWith(event.description, Specification.of(event.specification), Version.of(event.previousVersion), Version.of(event.nextVersion));
  }

  private void applySchemaVersionDescribed(SchemaVersionDescribed event) {
    this.state = state.withDescription(event.description);
  }

  private void applySchemaVersionAssigned(SchemaVersionAssigned event) {
    this.state = state.withVersion(Version.of(event.version));
  }

  private void applySchemaVersionSpecified(SchemaVersionSpecified event) {
    this.state = state.withSpecification(Specification.of(event.specification));
  }

  private void applySchemaVersionPublished(SchemaVersionPublished event) {
    this.state = state.asPublished();
  }

  private void applySchemaVersionDeprecated(SchemaVersionDeprecated event) {
    this.state = state.asDeprecated();
  }

  private void applySchemaVersionRemoved(SchemaVersionRemoved event) {
    this.state = state.asRemoved();
  }

  static {
    registerConsumer(SchemaVersionEntity.class, SchemaVersionDefined.class, SchemaVersionEntity::applySchemaVersionDefined);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionDescribed.class, SchemaVersionEntity::applySchemaVersionDescribed);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionAssigned.class, SchemaVersionEntity::applySchemaVersionAssigned);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionSpecified.class, SchemaVersionEntity::applySchemaVersionSpecified);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionPublished.class, SchemaVersionEntity::applySchemaVersionPublished);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionDeprecated.class, SchemaVersionEntity::applySchemaVersionDeprecated);
    registerConsumer(SchemaVersionEntity.class, SchemaVersionRemoved.class, SchemaVersionEntity::applySchemaVersionRemoved);
  }
}
