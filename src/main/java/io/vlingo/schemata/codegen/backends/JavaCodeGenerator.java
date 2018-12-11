package io.vlingo.schemata.codegen.backends;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.codegen.Backend;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaCodeGenerator implements Backend {
    @Override
    public String generateFrom(SchemaVersionDefinitionParser parser) {
        final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration = parser.typeDeclaration();

        final Class<?> baseClass = baseClassOf(typeDeclaration);
        final String typeName = typeNameOf(typeDeclaration);

        final TypeSpec.Builder spec = TypeSpec.classBuilder(typeName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .superclass(baseClass);

        final Stream<FieldSpec> fields = fieldsOf(parser);
        final TypeSpec eventClass = spec.addFields(fields.collect(Collectors.toList())).build();

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

    private Stream<FieldSpec> fieldsOf(SchemaVersionDefinitionParser parser) {
        return parser.typeDeclaration().typeBody().attributes().attribute().stream()
                .map(this::toFieldSpec);
    }

    private FieldSpec toFieldSpec(SchemaVersionDefinitionParser.AttributeContext context) {
        if (context.specialTypeAttribute() != null) {
            final SchemaVersionDefinitionParser.SpecialTypeAttributeContext field = context.specialTypeAttribute();
            return FieldSpec.builder(classOfSpecialType(field), field.IDENTIFIER().getSymbol().getText())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .build();
        } else if (context.basicTypeAttribute() != null) {
            return null;
        }

        return null;
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
