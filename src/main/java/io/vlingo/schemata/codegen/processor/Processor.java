package io.vlingo.schemata.codegen.processor;

import io.vlingo.schemata.codegen.ast.Node;

import java.util.concurrent.CompletableFuture;

public interface Processor {
    CompletableFuture<Node> process(Node node);
}
