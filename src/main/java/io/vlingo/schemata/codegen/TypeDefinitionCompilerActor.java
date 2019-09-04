package io.vlingo.schemata.codegen;

import io.vlingo.actors.Actor;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TypeDefinitionCompilerActor extends Actor implements TypeDefinitionCompiler {
    private final TypeParser parser;
    private final List<Processor> processors;
    private final Backend backend;

    public TypeDefinitionCompilerActor(final TypeParser parser, final List<Processor> processors, final Backend backend) {
        this.parser = parser;
        this.processors = processors;
        this.backend = backend;
    }

    public CompletableFuture<String> compile(final InputStream typeDefinition) {
        Function<Node, CompletableFuture<Node>> process = node -> {
            CompletableFuture<Node> result = CompletableFuture.completedFuture(node);
            for (Processor p : processors) {
                result = result.thenCompose(p::process);
            }

            return result;
        };

        CompletableFuture<String> result = completableFuture();

        parser.parseTypeDefinition(typeDefinition)
                .thenCompose(process)
                .thenCompose(backend::generateOutput)
                .thenAccept(result::complete);

        return result;
    }
}
