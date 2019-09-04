package io.vlingo.schemata.codegen.backend.java;

import io.vlingo.actors.Actor;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.backend.Backend;

import java.util.concurrent.CompletableFuture;

public class JavaBackend extends Actor implements Backend {
    @Override
    public CompletableFuture<String> generateOutput(Node node) {
        completableFuture().complete("XXX");
        return completableFuture();
    }
}
