// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backends.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Modifier;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.codegen.Backend;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;
import io.vlingo.schemata.codegen.backends.java.values.EventField;
import io.vlingo.schemata.codegen.backends.java.values.IntrinsicField;
import io.vlingo.schemata.codegen.backends.java.values.RuntimeSingleField;

public class JavaCodeGenerator implements Backend {
  @Override
  public String generateFrom(final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration) {
    final Class<?> baseClass = baseClassOf(typeDeclaration);
    final String typeName = typeNameOf(typeDeclaration);
    final List<EventField> foundEventFields = eventFieldsOf(typeName, typeDeclaration).collect(Collectors.toList());

    final List<CodeBlock> initializers = foundEventFields.stream().map(param -> CodeBlock.of(param.initialization()))
            .collect(Collectors.toList());

    final MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addParameters(toConstructorParameters(foundEventFields)).addCode(CodeBlock.join(initializers, "\n"))
            .build();

    final TypeSpec.Builder spec = TypeSpec.classBuilder(typeName).addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addMethod(constructor).superclass(baseClass);

    final TypeSpec eventClass = spec.addFields(toFieldSpecs(foundEventFields)).build();

    JavaFile javaFile = JavaFile.builder("my.package", eventClass).build();

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      javaFile.writeTo(new PrintStream(os));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return os.toString();
  }

  private Iterable<FieldSpec> toFieldSpecs(final List<EventField> foundEventFields) {
    return foundEventFields.stream().map(EventField::fieldDefinition)
            .map(tuple -> FieldSpec.builder(tuple._1, tuple._2, Modifier.FINAL, Modifier.PUBLIC).build())
            .collect(Collectors.toList());
  }

  private List<ParameterSpec> toConstructorParameters(final List<EventField> foundEventFields) {
    return foundEventFields.stream().map(EventField::parameterDefinition).filter(Optional::isPresent).map(Optional::get)
            .map(tuple -> ParameterSpec.builder(tuple._1, tuple._2, Modifier.FINAL).build())
            .collect(Collectors.toList());
  }

  private Class<?> baseClassOf(final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
    typeDeclarationContext.type().EVENT().getSymbol().getText();
    return DomainEvent.class;
  }

  private String typeNameOf(final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
    return typeDeclarationContext.typeName().TYPE_IDENTIFIER().getSymbol().getText();
  }

  private Stream<EventField> eventFieldsOf(
          final String typeName,
          final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration) {
    return typeDeclaration.typeBody().attributes().attribute().stream()
            .map(attribute -> this.toEventField(typeName, attribute));
  }

  private EventField toEventField(final String typeName, final SchemaVersionDefinitionParser.AttributeContext field) {
    if (field.specialTypeAttribute() != null) {
      return IntrinsicField.of(typeName,
              firstNotNull(field.specialTypeAttribute().TIMESTAMP(), field.specialTypeAttribute().VERSION(),
                      field.specialTypeAttribute().TYPE()),
              field.specialTypeAttribute().IDENTIFIER().getSymbol().getText());
    } else if (field.basicTypeAttribute() != null) {
      return RuntimeSingleField.of(typeName, firstNotNull(field.basicTypeAttribute().BOOLEAN(),
              field.basicTypeAttribute().BYTE(), field.basicTypeAttribute().CHAR(), field.basicTypeAttribute().DOUBLE(),
              field.basicTypeAttribute().FLOAT(), field.basicTypeAttribute().INT(), field.basicTypeAttribute().LONG(),
              field.basicTypeAttribute().SHORT(), field.basicTypeAttribute().STRING()),
              field.basicTypeAttribute().IDENTIFIER().getSymbol().getText(), null);
    } else {
      return null;
    }
  }

  private String firstNotNull(final TerminalNode... nodes) {
    return Arrays.stream(nodes).filter(Objects::nonNull).map(e -> e.getSymbol().getText()).findFirst().orElse("<unk>");
  }
}
