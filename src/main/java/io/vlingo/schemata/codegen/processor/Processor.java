package io.vlingo.schemata.codegen.processor;

import io.vlingo.schemata.codegen.ast.Node;

import java.util.concurrent.CompletableFuture;

public interface Processor {
    CompletableFuture<Node> process(Node node);

    static <T extends Node> T requireBeing(Node node, Class<T> nodeClass) {
        if (nodeClass.isInstance(node)) {
            return (T) node;
        }

        throw new IllegalArgumentException(node + " is not of type " + nodeClass.getCanonicalName() + " but type " + node.getClass().getCanonicalName());
    }
}
