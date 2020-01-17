// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.ComputableType;
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.ast.values.Value;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.resource.data.SchemaVersionData;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Map;

public final class SchemaVersionEntity extends ObjectEntity<SchemaVersionState> implements SchemaVersion {
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

  @Override
  public Completes<SpecificationDiff> diff(final TypeDefinitionMiddleware typeDefinitionMiddleware, final SchemaVersionData other) {
    requireRightSideSpecification(other.specification);

    SpecificationDiff diff = SpecificationDiff.between(stateObject().specification.value, other.specification);

    // FIXME: Make reactive, don't await() the node; but using andThenTo() instead of andThen().await() has been hanging
    TypeDefinition leftType = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(stateObject().specification.value.getBytes(StandardCharsets.UTF_8)),
        null)
        .andThen(SchemaVersionEntity::asTypeDefinition)
        .await();

    TypeDefinition rightType = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(other.specification.getBytes(StandardCharsets.UTF_8)),
        null)
        .andThen(SchemaVersionEntity::asTypeDefinition)
        .await();

    if (!leftType.typeName.equals(rightType.typeName))
      diff = diff.withChange(Change.ofType(leftType.typeName, rightType.typeName));


    // TODO: refactor
    for (int i = 0; i < leftType.children.size(); i++) {
      FieldDefinition l = asFieldDefinition(leftType.children.get(i));

      if(rightType.children.size() > i) {
        FieldDefinition r = asFieldDefinition(rightType.children.get(i));

        if (!(l.name.equals(r.name) && l.defaultValue.equals(r.defaultValue) && l.version.equals(r.version))) {
          diff = diff.withChange(Change.ofField(l.name, r.name));
        }

        Type leftFieldType = l.type;
        Type rightFieldType = r.type;

        if (rightFieldType.getClass() != leftFieldType.getClass()) {
          diff = diff.withChange(Change.ofType(
            String.format("%s (%s)", leftType.typeName, leftType.getClass().getSimpleName()),
            String.format("%s (%s)", rightType.typeName, rightType.getClass().getSimpleName())
          ));
        } else {
          diff = addFieldTypeDiffs(diff, leftFieldType, rightFieldType);
        }
      } else {
        diff = diff.withChange(Change.removalOfField(l.name));
      }

      if (i == leftType.children.size()-1) { // we're done with the left side, now process additions
        for (int j = i; j < rightType.children.size(); j++) {
          FieldDefinition r = asFieldDefinition(rightType.children.get(j));
          diff = diff.withChange(Change.additionOfField(r.name));
        }
      }

    }

    return completes().with(diff);
  }

  private SpecificationDiff addFieldTypeDiffs(SpecificationDiff diff, Type leftFieldType, Object rightFieldType) {
    if (leftFieldType instanceof BasicType
        && !((BasicType) leftFieldType).typeName.equals(((BasicType) rightFieldType).typeName)) {
      diff = diff.withChange(Change.ofType(
          ((BasicType) leftFieldType).typeName,
          ((BasicType) rightFieldType).typeName));

    } else if (leftFieldType instanceof ComputableType
        && !((ComputableType) leftFieldType).typeName.equals(((ComputableType) rightFieldType).typeName)
    ) {
      diff = diff.withChange(Change.ofField(
          ((ComputableType) leftFieldType).typeName,
          ((ComputableType) rightFieldType).typeName));
    } else if (leftFieldType instanceof TypeDefinition
        && !((TypeDefinition) leftFieldType).fullyQualifiedTypeName.equals(((TypeDefinition) rightFieldType).fullyQualifiedTypeName)
    ) {
      diff = diff.withChange(Change.ofType(
          ((TypeDefinition) leftFieldType).fullyQualifiedTypeName,
          ((TypeDefinition) rightFieldType).fullyQualifiedTypeName));
    }
    return diff;
  }

  private static void requireRightSideSpecification(String specification) {
    if (specification == null || specification.isEmpty()) {
      throw new IllegalArgumentException("Can't compare to an empty specification");
    }
  }

  private static TypeDefinition asTypeDefinition(Node n) {
    return Processor.requireBeing(n, TypeDefinition.class);
  }

  private static FieldDefinition asFieldDefinition(Node n) {
    return Processor.requireBeing(n, FieldDefinition.class);
  }
}
