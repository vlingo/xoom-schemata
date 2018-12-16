package io.vlingo.schemata.codegen;

import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;

public interface Backend {
    String generateFrom(SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration);
}
