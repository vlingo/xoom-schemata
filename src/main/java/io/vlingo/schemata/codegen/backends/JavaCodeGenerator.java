package io.vlingo.schemata.codegen.backends;

import com.squareup.javapoet.*;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.codegen.Backend;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaCodeGenerator implements Backend {
    @Override
    public String generateFrom(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration) {
        final Class<?> baseClass = baseClassOf(typeDeclaration);
        final String typeName = typeNameOf(typeDeclaration);

        final List<ParameterSpec> constructorParams = new ArrayList<>();
        final List<FieldSpec> fields = fieldsOf(typeDeclaration, constructorParams).collect(Collectors.toList());

        final List<CodeBlock> initializers = constructorParams.stream()
                .map(param -> CodeBlock.of(String.format("this.%s = %s;\n", param.name, param.name)))
                .collect(Collectors.toList());

        final MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameters(constructorParams)
                .addCode(CodeBlock.join(initializers, ""))
                .build();

        final TypeSpec.Builder spec = TypeSpec.classBuilder(typeName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(constructor)
                .superclass(baseClass);

        final TypeSpec eventClass = spec.addFields(fields).build();

        JavaFile javaFile = JavaFile.builder("my.package", eventClass)
                .build();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            javaFile.writeTo(new PrintStream(os));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return os.toString();
    }

    private Class<?> baseClassOf(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
        typeDeclarationContext.type().EVENT().getSymbol().getText();
        return DomainEvent.class;
    }

    private String typeNameOf(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
        return typeDeclarationContext.typeName().TYPE_IDENTIFIER().getSymbol().getText();
    }

    private Stream<FieldSpec> fieldsOf(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext, List<ParameterSpec> constructorParams) {
        return typeDeclarationContext.typeBody().attributes().attribute().stream()
                .map(field -> toFieldSpec(field, constructorParams));
    }

    private FieldSpec toFieldSpec(SchemaVersionDefinitionParser.AttributeContext context, List<ParameterSpec> constructorParams) {
        if (context.specialTypeAttribute() != null) {
            final SchemaVersionDefinitionParser.SpecialTypeAttributeContext field = context.specialTypeAttribute();
            return FieldSpec.builder(classOfSpecialType(field), field.IDENTIFIER().getSymbol().getText())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .build();
        } else if (context.basicTypeAttribute() != null) {
            final SchemaVersionDefinitionParser.BasicTypeAttributeContext field = context.basicTypeAttribute();

            constructorParams.add(ParameterSpec.builder(classOfBasicType(field), field.IDENTIFIER().getSymbol().getText()).build());

            return FieldSpec.builder(classOfBasicType(field), field.IDENTIFIER().getSymbol().getText())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .build();
        }

        return null;
    }

    private Class<?> classOfBasicType(SchemaVersionDefinitionParser.BasicTypeAttributeContext field) {
        return String.class;
    }

    private Class<?> classOfSpecialType(SchemaVersionDefinitionParser.SpecialTypeAttributeContext context) {
        if (context.TYPE() != null) {
            return String.class;
        } else if (context.TIMESTAMP() != null) {
            return Long.class;
        } else if (context.VERSION() != null) {
            return Integer.class;
        } else {
            return Object.class;
        }
    }
}
