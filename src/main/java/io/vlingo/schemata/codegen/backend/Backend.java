package io.vlingo.schemata.codegen.backend;

import io.vlingo.schemata.codegen.ast.Node;

import java.util.concurrent.CompletableFuture;

public interface Backend {
    CompletableFuture<String> generateOutput(Node node, String version);
}
