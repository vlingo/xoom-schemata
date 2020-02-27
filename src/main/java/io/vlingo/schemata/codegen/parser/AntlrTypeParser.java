// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.parser;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionLexer;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;
import io.vlingo.schemata.codegen.ast.FieldDefinition;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.ast.types.BasicArrayType;
import io.vlingo.schemata.codegen.ast.types.BasicType;
import io.vlingo.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.schemata.codegen.ast.values.ListValue;
import io.vlingo.schemata.codegen.ast.values.NullValue;
import io.vlingo.schemata.codegen.ast.values.SingleValue;
import io.vlingo.schemata.codegen.ast.values.Value;
import io.vlingo.schemata.model.Category;


public class AntlrTypeParser extends Actor implements TypeParser {
    private static final int BUFFER_SIZE = 2048;
    private final byte[] parserBuffer;

    public AntlrTypeParser() {
        parserBuffer = new byte[BUFFER_SIZE];
    }

    @Override
    public Completes<Node> parseTypeDefinition(final InputStream inputStream, final String fullyQualifiedTypeName) {
        CompletesEventually eventually = completesEventually();
        SchemaVersionDefinitionParser tree;

        try {
            ParserErrorStrategy errorStrategy = new ParserErrorStrategy();
            tree = generateAntlrTree(inputStream, errorStrategy);
            Node type = parseTypeDeclaration(tree.typeDeclaration(), fullyQualifiedTypeName);
            if(errorStrategy.hasErrors()) {
                errorStrategy.errors().forEach(e -> logger().error(e.getMessage(),e));
                throw new ParseException(String.format("Parsing %s schema failed", fullyQualifiedTypeName), errorStrategy.errors());
            } else {
                eventually.with(type);
            }
        } catch (IOException e) {
            logger().error(e.getMessage(), e);
            eventually.with(null);
        }

        return completes();
    }

    private Node parseTypeDeclaration(
            final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration,
            final String fullyQualifiedTypeName) {

        String typeKind = typeDeclaration.type().getText();
        String typeName = typeDeclaration.typeName().getText();

        Stream<Node> fields = typeDeclaration.typeBody().attributes().attribute().stream().map(this::parseFieldDefinition);

        return new TypeDefinition(categoryOf(typeKind), fullyQualifiedTypeName, typeName, fields.collect(toList()));
    }

    private Node parseFieldDefinition(SchemaVersionDefinitionParser.AttributeContext attribute) {
        if (attribute.basicTypeAttribute() != null) {
            return parseBasicTypeAttribute(attribute.basicTypeAttribute());
        }

        if (attribute.complexTypeAttribute() != null) {
            return parseComplexTypeAttribute(attribute.complexTypeAttribute());
        }

        return parseSpecialTypeAttribute(attribute.specialTypeAttribute());
    }

    @SuppressWarnings("rawtypes")
    private Node parseBasicTypeAttribute(SchemaVersionDefinitionParser.BasicTypeAttributeContext attribute) {
        String typeName = firstNotNull(attribute.BOOLEAN(),
                attribute.BYTE(), attribute.CHAR(), attribute.DOUBLE(),
                attribute.FLOAT(), attribute.INT(), attribute.LONG(),
                attribute.SHORT(), attribute.STRING());

        boolean isArrayType = attribute.ARRAY() == null ? false : true;

        String fieldName = attribute.IDENTIFIER().getText();

        Optional<Value> defaultValue = Optional.of(NullValue.get());
        switch(typeName) {
            case "boolean":
                defaultValue = Optional.of(nodeValueOf(attribute.BOOLEAN_LITERAL()));
                break;
            case "byte":
                defaultValue = Optional.of(nodeValueOf(attribute.BYTE_LITERAL()));
                break;
            case "char":
                defaultValue = Optional.of(nodeValueOf(attribute.CHAR_LITERAL()));
                break;
            case "double":
            case "float":
                defaultValue = Optional.of(nodeValueOf(attribute.FLOAT_LITERAL()));
                break;
            case "int":
            case "long":
            case "short":
                defaultValue = Optional.of(nodeValueOf(attribute.DECIMAL_LITERAL()));
                break;
            case "string":
               defaultValue = Optional.of(nodeValueOf(attribute.STRING_LITERAL()));
               break;
        }

        return new FieldDefinition(
                isArrayType ? new BasicArrayType(typeName) : new BasicType(typeName),
                Optional.empty(),
                fieldName,
                defaultValue);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Value nodeValueOf(List<TerminalNode> literals) {
        switch (literals.size()) {
            case 0:
                return new NullValue();
            case 1:
                return nodeValueOf(literals.get(0));
            default: // we have an array type here
                return new ListValue(literals.stream().map(this::nodeValueOf).collect(toList()));
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Value nodeValueOf(TerminalNode literal) {
        return literal == null
                ? new NullValue()
                : new SingleValue(literal.getText());
    }

    private Node parseComplexTypeAttribute(SchemaVersionDefinitionParser.ComplexTypeAttributeContext attribute) {
        String typeName = attribute.typeName().getText();
        String fieldName = attribute.IDENTIFIER().getText();

        return new FieldDefinition(new BasicType(typeName), Optional.empty(), fieldName, Optional.empty());
    }

    private Node parseSpecialTypeAttribute(SchemaVersionDefinitionParser.SpecialTypeAttributeContext attribute) {
        String typeName = firstNotNull(attribute.TIMESTAMP(), attribute.TYPE(), attribute.VERSION());
        String fieldName = attribute.IDENTIFIER().getText();

        return new FieldDefinition(new BasicType(typeName), Optional.empty(), fieldName, Optional.empty());
    }

    private Category categoryOf(String typeKind) {
        return Arrays.stream(Category.values()).filter(category -> category.name().equalsIgnoreCase(typeKind))
                .findFirst()
                .get();
    }

    private SchemaVersionDefinitionParser generateAntlrTree(InputStream inputStream, ParserErrorStrategy errorStrategy) throws IOException {
        CodePointBuffer buffer = CodePointBuffer.withBytes(consume(inputStream));
        CodePointCharStream in = CodePointCharStream.fromBuffer(buffer);
        SchemaVersionDefinitionLexer lexer = new SchemaVersionDefinitionLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SchemaVersionDefinitionParser parser =new SchemaVersionDefinitionParser(tokens);
        parser.setErrorHandler(errorStrategy);
        return parser;
    }

    private ByteBuffer consume(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int nRead;

        while ((nRead = inputStream.read(parserBuffer, 0, parserBuffer.length)) != -1) {
            bos.write(parserBuffer, 0, nRead);
        }

        return ByteBuffer.wrap(bos.toByteArray());
    }

    private String firstNotNull(final TerminalNode... nodes) {
        return Arrays.stream(nodes).filter(Objects::nonNull).map(e -> e.getSymbol().getText()).findFirst().orElse("<unk>");
    }
}
