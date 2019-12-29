// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import com.google.gson.internal.Streams;
import io.vlingo.common.Completes;
import io.vlingo.lattice.model.object.ObjectEntity;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.model.Id.SchemaVersionId;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    // FIXME: Make reactive, don't await the node but break up into andThens
    Node leftNode = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(specification.value.getBytes(StandardCharsets.UTF_8)),
        null).await();

    Node rightNode = typeDefinitionMiddleware.compileToAST(
        new ByteArrayInputStream(specification.value.getBytes(StandardCharsets.UTF_8)),
        null).await();

    TypeDefinition leftType = Processor.requireBeing(leftNode, TypeDefinition.class);
    TypeDefinition rightType = Processor.requireBeing(rightNode, TypeDefinition.class);

    if (!leftType.typeName.equals(rightType.typeName))
      return completes().with(SpecificationDiff.incompatibleDiff());
    
    return completes().with(
        SpecificationDiff.of(zip(leftType.children.stream(),
            rightType.children.stream(),
            AbstractMap.SimpleEntry::new)
            .allMatch(x -> {
              FieldDefinition l = Processor.requireBeing(x.getKey(), FieldDefinition.class);
              FieldDefinition r = Processor.requireBeing(x.getValue(), FieldDefinition.class);

              return l.name.equals(r.name)
                  // FIXME: compare types
                  && l.defaultValue.equals(r.defaultValue)
                  && l.version.equals(r.version);
            })
        )
    );
  }

  // Move to common?
  public static <L, R, T> Stream<T> zip(Stream<L> leftStream, Stream<R> rightStream, BiFunction<L, R, T> combiner) {
    Spliterator<L> lefts = leftStream.spliterator();
    Spliterator<R> rights = rightStream.spliterator();
    return StreamSupport.stream(
        new Spliterators.AbstractSpliterator<T>(
            Long.min(lefts.estimateSize(), rights.estimateSize()),
            lefts.characteristics() & rights.characteristics()) {
          @Override
          public boolean tryAdvance(Consumer<? super T> action) {
            return lefts.tryAdvance(left -> rights.tryAdvance(right -> action.accept(combiner.apply(left, right))));
          }
        }, leftStream.isParallel() || rightStream.isParallel());
  }
}
