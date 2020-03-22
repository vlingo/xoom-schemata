// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.*;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDeprecated;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public final class SchemaVersionEntity extends ObjectEntity<SchemaVersionState> implements SchemaVersion {
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

  @Override
  public Completes<Outcome<SchemataBusinessException, SpecificationDiff>> diff(final TypeDefinitionMiddleware typeDefinitionMiddleware, final SchemaVersionData other) {
    final CompletesEventually completesEventually = completesEventually();

    if (other.specification == null || other.specification.isEmpty()) {
      completesEventually.with(Failure.of(SchemataBusinessException.invalidSchemaDefinition()));
      return completes();
    }

    SpecificationDiff diff = SpecificationDiff.between(stateObject().specification.value, other.specification);

    // FIXME: Make reactive, don't await() the node
    Outcome<SchemataBusinessException,Node> leftAst = typeDefinitionMiddleware.compileToAST(
            new ByteArrayInputStream(stateObject().specification.value.getBytes(StandardCharsets.UTF_8)),
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

  private static TypeDefinition asTypeDefinition(Node n) {
    return Processor.requireBeing(n, TypeDefinition.class);
  }

  private static FieldDefinition asFieldDefinition(Node n) {
    return Processor.requireBeing(n, FieldDefinition.class);
  }

}
