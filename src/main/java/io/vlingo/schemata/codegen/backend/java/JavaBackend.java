// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backend.java;

import com.squareup.javapoet.*;
import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.ComputableType;
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.processor.Processor;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public class JavaBackend extends Actor implements Backend {
    public JavaBackend() {
    }

    @Override
    public Completes<String> generateOutput(Node node, String version) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);
        completesEventually().with(compileJavaClass(type, version));
        return completes();
    }

    private String compileJavaClass(TypeDefinition type, String version) {
        final Class<?> baseClass = baseClassOf(type);
        final String typeName = type.typeName;
        final String typeReference = type.fullyQualifiedTypeName;
        final String category = type.category.name().toLowerCase();

        final TypeSpec eventClass = getTypeSpec(type, version, baseClass, typeName);
        JavaFile javaFile = JavaFile.builder(packageOf(category, typeReference), eventClass).build();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            javaFile.writeTo(new PrintStream(os));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return os.toString();
    }

    private TypeSpec getTypeSpec(TypeDefinition type, String version, Class<?> baseClass, String typeName) {
        final List<FieldDefinition> fields = type.children.stream().map(e -> (FieldDefinition) e).collect(Collectors.toList());

        final List<CodeBlock> initializers = fields.stream()
                .map(param -> CodeBlock.of(initializationOf(param, type, version)))
                .collect(Collectors.toList());

        final List<FieldSpec> classFields = fields.stream()
                .map(this::toField)
                .collect(Collectors.toList());
        ;

        final MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameters(fields.stream().filter(field -> !(field.type instanceof ComputableType)).map(this::toConstructorParameter).collect(Collectors.toList()))
                .addCode(CodeBlock.join(initializers, "\n"))
                .build();

        final TypeSpec.Builder spec = TypeSpec.classBuilder(unqualifiedName(typeName))
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(constructor).superclass(baseClass);

        return spec.addFields(classFields).build();
    }

    private String initializationOf(FieldDefinition definition, TypeDefinition owner, String version) {
        Type type = definition.type;
        if (type instanceof ComputableType) {
            switch (((ComputableType) type).typeName) {
                case "type":
                    return String.format("this.%s = \"%s\";", definition.name, owner.typeName);
                case "version":
                    return String.format("this.%s = io.vlingo.common.version.SemanticVersion.toValue(\"%s\");", definition.name, version);
                case "timestamp":
                    return String.format("this.%s = System.currentTimeMillis();", definition.name);
            }
        }

        return String.format("this.%s = %s; ", definition.name, definition.name);
    }

    private FieldSpec toField(FieldDefinition definition) {
        Type type = definition.type;
        Modifier[] modifiers = definition.hasDefaultValue()
          ? new Modifier[] { Modifier.PUBLIC }
          : new Modifier[] {Modifier.FINAL, Modifier.PUBLIC};

        if (type instanceof BasicType) {
            FieldSpec.Builder builder =
             FieldSpec
              .builder(
                primitive((BasicType) type),
                definition.name,
                modifiers);

            if(definition.hasDefaultValue()) {
                builder = builder.initializer("$L", definition.defaultValue.get().value());
            }

              return builder.build();
        }

        if (type instanceof ComputableType) {
            return FieldSpec.builder(computable((ComputableType) type), definition.name, Modifier.FINAL, Modifier.PUBLIC).build();
        }

        if (type instanceof TypeDefinition) {
            ClassName className = ClassName.bestGuess(((TypeDefinition) type).typeName);
            return FieldSpec.builder(className, definition.name, Modifier.FINAL, Modifier.PUBLIC).build();
        }

        return null;
    }

    private ParameterSpec toConstructorParameter(FieldDefinition definition) {
        Type type = definition.type;
        if (type instanceof BasicType) {
            return ParameterSpec.builder(primitive((BasicType) type), definition.name, Modifier.FINAL).build();
        } else {
            ClassName className = ClassName.bestGuess(((TypeDefinition) type).typeName);
            return ParameterSpec.builder(className, definition.name, Modifier.FINAL).build();
        }
    }

    private String packageOf(String category, String reference) {
        String[] referenceParts = reference.split(Schemata.ReferenceSeparator);
        if (referenceParts.length < Schemata.MinReferenceParts) {
            throw new IllegalArgumentException("Invalid fully qualified type name. Valid type names look like this <organization>:<unit>:<context namespace>:<type name>[:<version>].");
        }
        final String namespace = referenceParts[2];
        final String className = referenceParts[3];

        final String basePackage = namespace + "." + category;
        final String localPackage = className.contains(".") ? className.substring(0, className.lastIndexOf('.')) : "";
        return localPackage.length() > 0
                ? basePackage + "." + localPackage
                : basePackage;
    }

    private String unqualifiedName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    private TypeName primitive(BasicType basicType) {
        switch (basicType.typeName) {
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "short":
                return TypeName.SHORT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "float":
                return TypeName.FLOAT;
            case "double":
                return TypeName.DOUBLE;
            case "string":
                return TypeName.get(String.class);
            default:
                return TypeName.get(Object.class);
        }
    }

    private TypeName computable(ComputableType computableType) {
        switch (computableType.typeName) {
            case "type":
                return TypeName.get(String.class);
            case "timestamp":
                return TypeName.LONG;
            case "version":
                return TypeName.INT;
            default:
                return TypeName.get(Object.class);
        }
    }

    private Class<?> baseClassOf(TypeDefinition type) {
        switch (type.category) {
            case Event:
                return DomainEvent.class;
            default:
                return DomainEvent.class;
        }
    }
}
