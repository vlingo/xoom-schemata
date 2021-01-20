// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backend.java;

import static java.util.stream.Collectors.joining;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.ComputableType;
import io.vlingo.schemata.codegen.ast.types.Type;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.ast.values.ListValue;
import io.vlingo.schemata.codegen.ast.values.NullValue;
import io.vlingo.schemata.codegen.ast.values.SingleValue;
import io.vlingo.schemata.codegen.ast.values.Value;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.errors.SchemataBusinessException;

@SuppressWarnings("rawtypes")
public class JavaBackend implements Backend {
    public JavaBackend() {
    }

    @Override
    public Outcome<SchemataBusinessException, String> generateOutput(Node node, String version) {
        TypeDefinition type = Processor.requireBeing(node, TypeDefinition.class);
        return compileJavaClass(type, version);
    }

    private Outcome<SchemataBusinessException, String> compileJavaClass(TypeDefinition type, String version) {
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
            return Failure.of(SchemataBusinessException.codeGenerationError(e));
        }

        return Success.of(os.toString());
    }

    private TypeSpec getTypeSpec(TypeDefinition type, String version, Class<?> baseClass, String typeName) {
        final List<FieldDefinition> fields = type.children.stream().map(e -> (FieldDefinition) e).collect(Collectors.toList());

        final List<CodeBlock> initializers = fields.stream()
                .map(param -> CodeBlock.of(initializationOf(param, type, version)))
                .collect(Collectors.toList());

        final List<CodeBlock> computedFieldInitializers = fields.stream()
                .filter(field -> (field.type instanceof ComputableType))
                .map(param -> CodeBlock.of(initializationOf(param, type, version)))
                .collect(Collectors.toList());

        final List<FieldSpec> classFields = fields.stream()
                .map(this::toField)
                .collect(Collectors.toList());
        ;

        final MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameters(fields.stream().filter(field -> !(field.type instanceof ComputableType)).map(this::toConstructorParameter).collect(Collectors.toList()))
                .addCode(CodeBlock.join(initializers, ""))
                .build();

        final MethodSpec noArgConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.join(computedFieldInitializers, ""))
                .build();

        final TypeSpec.Builder spec = TypeSpec.classBuilder(unqualifiedName(typeName))
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(constructor)
                .superclass(baseClass);

        if(constructor.parameters.size() != 0
            && fields.stream().allMatch(f -> f.hasDefaultValue() || f.type instanceof ComputableType)) {
            spec.addMethod(noArgConstructor);
        }



        return spec.addFields(classFields).build();
    }

    private String initializationOf(FieldDefinition definition, TypeDefinition owner, String version) {
        Type type = definition.type;
        if (type instanceof ComputableType) {
            switch (((ComputableType) type).typeName) {
                case "type":
                    return String.format("this.%s = \"%s\";\n", definition.name, owner.typeName);
                case "version":
                    return String.format("this.%s = io.vlingo.common.version.SemanticVersion.toValue(\"%s\");\n", definition.name, version);
                case "timestamp":
                    return String.format("this.%s = System.currentTimeMillis();\n", definition.name);
            }
        }

        return String.format("this.%s = %s;\n", definition.name, definition.name);
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
                builder = builder.initializer("$L", javaLiteralOf(definition));
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

    private String javaLiteralOf(FieldDefinition definition) {
        Value value = definition.defaultValue.orElseGet(NullValue::new);

        if(value instanceof NullValue) {
            return null;
        }

        if(value instanceof SingleValue) {
            return javaLiteralOf((SingleValue) value);
        }

        if(value instanceof ListValue) {
            return javaLiteralOf(definition.type, (ListValue) value);
        }

        throw new IllegalStateException("Unsupported value type encountered");
    }

    private String javaLiteralOf(SingleValue value) {
        return value.value().toString();
    }

    @SuppressWarnings("unchecked")
    private String javaLiteralOf(Type type, ListValue value) {
        return value.value().stream()
                .map(e -> ((SingleValue)e).value())
                .collect(joining(
                        ", ",
                        String.format("new %s { ", typeTypeOf(type)),
                        " }"
                )).toString();
    }

    private ParameterSpec toConstructorParameter(FieldDefinition definition) {
        return ParameterSpec.builder(typeTypeOf(definition.type), definition.name, Modifier.FINAL).build();
    }

    private TypeName typeTypeOf(Type type) {
        if (type instanceof BasicType) {
            return primitive((BasicType) type);
        } else {
            return ClassName.bestGuess(((TypeDefinition) type).typeName);
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
        TypeName result = null;
        switch (basicType.typeName) {
            case "boolean":
                result = TypeName.BOOLEAN;
                break;
            case "byte":
                result = TypeName.BYTE;
                break;
            case "char":
                result = TypeName.CHAR;
                break;
            case "short":
                result = TypeName.SHORT;
                break;
            case "int":
                result = TypeName.INT;
                break;
            case "long":
                result = TypeName.LONG;
                break;
            case "float":
                result = TypeName.FLOAT;
                break;
            case "double":
                result = TypeName.DOUBLE;
                break;
            case "string":
                result = TypeName.get(String.class);
                break;
            default:
                result = TypeName.get(Object.class);
                break;
        }

        return basicType.isArrayType() ? ArrayTypeName.of(result) : result;
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
