package io.vlingo.schemata.codegen.parser;

import io.vlingo.schemata.codegen.ast.Node;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface TypeParser {
    CompletableFuture<Node> parseTypeDefinition(InputStream inputStream);
}
