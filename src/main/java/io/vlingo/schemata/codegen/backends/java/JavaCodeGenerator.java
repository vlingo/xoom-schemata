package io.vlingo.schemata.codegen.backends.java;

import com.squareup.javapoet.*;
import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.codegen.Backend;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;
import io.vlingo.schemata.codegen.backends.java.values.EventField;
import io.vlingo.schemata.codegen.backends.java.values.IntrinsicField;
import io.vlingo.schemata.codegen.backends.java.values.RuntimeSingleField;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaCodeGenerator implements Backend {
    @Override
    public String generateFrom(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration) {
        final Class<?> baseClass = baseClassOf(typeDeclaration);
        final String typeName = typeNameOf(typeDeclaration);
        final List<EventField> foundEventFields = eventFieldsOf(typeName, typeDeclaration).collect(Collectors.toList());

        final List<CodeBlock> initializers = foundEventFields.stream()
                .map(param -> CodeBlock.of(param.initialization()))
                .collect(Collectors.toList());

        final MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addParameters(toConstructorParameters(foundEventFields))
                .addCode(CodeBlock.join(initializers, "\n"))
                .build();

        final TypeSpec.Builder spec = TypeSpec.classBuilder(typeName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addMethod(constructor)
                .superclass(baseClass);

        final TypeSpec eventClass = spec.addFields(toFieldSpecs(foundEventFields)).build();

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

    private Iterable<FieldSpec> toFieldSpecs(List<EventField> foundEventFields) {
        return foundEventFields.stream()
                .map(EventField::fieldDefinition)
                .map(tuple -> FieldSpec.builder(tuple._1, tuple._2, Modifier.FINAL, Modifier.PUBLIC).build())
                .collect(Collectors.toList());
    }

    private List<ParameterSpec> toConstructorParameters(List<EventField> foundEventFields) {
        return foundEventFields.stream()
                .map(EventField::parameterDefinition)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(tuple -> ParameterSpec.builder(tuple._1, tuple._2, Modifier.FINAL).build())
                .collect(Collectors.toList());
    }

    private Class<?> baseClassOf(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
        typeDeclarationContext.type().EVENT().getSymbol().getText();
        return DomainEvent.class;
    }

    private String typeNameOf(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclarationContext) {
        return typeDeclarationContext.typeName().TYPE_IDENTIFIER().getSymbol().getText();
    }

    private Stream<EventField> eventFieldsOf(String typeName, SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration) {
        return typeDeclaration.typeBody().attributes().attribute().stream()
                .map(attribute -> this.toEventField(typeName, attribute));
    }

    private EventField toEventField(String typeName, SchemaVersionDefinitionParser.AttributeContext field) {
        if (field.specialTypeAttribute() != null) {
            return IntrinsicField.of(
                    typeName,
                    firstNotNull(
                            field.specialTypeAttribute().TIMESTAMP(),
                            field.specialTypeAttribute().VERSION(),
                            field.specialTypeAttribute().TYPE()
                    ),
                    field.specialTypeAttribute().IDENTIFIER().getSymbol().getText()
            );
        } else if (field.basicTypeAttribute() != null) {
            return RuntimeSingleField.of(
                    typeName,
                    firstNotNull(
                            field.basicTypeAttribute().BOOLEAN(),
                            field.basicTypeAttribute().BYTE(),
                            field.basicTypeAttribute().CHAR(),
                            field.basicTypeAttribute().DOUBLE(),
                            field.basicTypeAttribute().FLOAT(),
                            field.basicTypeAttribute().INT(),
                            field.basicTypeAttribute().LONG(),
                            field.basicTypeAttribute().SHORT(),
                            field.basicTypeAttribute().STRING()
                    ),
                    field.basicTypeAttribute().IDENTIFIER().getSymbol().getText(),
                    null
            );
        } else {
            return null;
        }
    }

    private String firstNotNull(TerminalNode... nodes) {
        return Arrays.stream(nodes).filter(Objects::nonNull).map(e -> e.getSymbol().getText()).findFirst().orElse("<unk>");
    }
}
