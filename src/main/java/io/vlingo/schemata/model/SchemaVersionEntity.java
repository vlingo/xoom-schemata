// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.model.Id.SchemaVersionId;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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
  public Completes<SpecificationDiff> isCompatibleWith(final TypeDefinitionMiddleware typeDefinitionMiddleware, final Specification specification) {
    requireRightSideSpecification(specification);
    // FIXME: Make reactive, don't await() the node; but this has been hanging
    Node leftNode = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(stateObject().specification.value.getBytes(StandardCharsets.UTF_8)),
        null).await();

    Node rightNode = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(specification.value.getBytes(StandardCharsets.UTF_8)),
        null).await();

    TypeDefinition leftType = Processor.requireBeing(leftNode, TypeDefinition.class);
    TypeDefinition rightType = Processor.requireBeing(rightNode, TypeDefinition.class);

    if (!leftType.typeName.equals(rightType.typeName))
      return completes().with(SpecificationDiff.incompatibleDiff());

    for (int i = 0; i < leftType.children.size(); i++) {
      FieldDefinition l = Processor.requireBeing(leftType.children.get(i), FieldDefinition.class);
      FieldDefinition r = Processor.requireBeing(rightType.children.get(i), FieldDefinition.class);

      if (!(l.name.equals(r.name)
          && l.defaultValue.equals(r.defaultValue)
          && l.version.equals(r.version))) {
        return completes().with(SpecificationDiff.incompatibleDiff());
      }


      Type leftFieldType = l.type;
      Type rightFieldType = r.type;

      if (rightFieldType.getClass() != leftFieldType.getClass()) {
        return completes().with(SpecificationDiff.incompatibleDiff());
      }

      if (leftFieldType instanceof BasicType
          && !((BasicType) leftFieldType).typeName.equals(((BasicType) rightFieldType).typeName)) {
        return completes().with(SpecificationDiff.incompatibleDiff());

      } else if (leftFieldType instanceof ComputableType
          && !((ComputableType) leftFieldType).typeName.equals(((ComputableType) rightFieldType).typeName)
      ) {
        return completes().with(SpecificationDiff.incompatibleDiff());
      } else if (leftFieldType instanceof TypeDefinition
          && !((TypeDefinition) leftFieldType).fullyQualifiedTypeName.equals(((TypeDefinition) rightFieldType).fullyQualifiedTypeName)
      ) {
        return completes().with(SpecificationDiff.incompatibleDiff());
        // TODO: check compatibility based on semantic version
      }


    }

    return completes().with(SpecificationDiff.compatibleDiff());
  }

  private void requireRightSideSpecification(Specification specification) {
    if (specification == null || specification.value == null || specification.value.isEmpty()) {
      throw new IllegalArgumentException("Can't compare to an empty specification");
    }
  }
}
