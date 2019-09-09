// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import io.vlingo.actors.Actor;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;

public class TypeDefinitionCompilerActor extends Actor implements TypeDefinitionCompiler {
    private final TypeParser parser;
    private final List<Processor> processors;
    private final Backend backend;

    public TypeDefinitionCompilerActor(final TypeParser parser, final List<Processor> processors, final Backend backend) {
        this.parser = parser;
        this.processors = processors;
        this.backend = backend;
    }

    @Override
    public CompletableFuture<String> compile(final InputStream typeDefinition, final String version) {
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
                .thenCompose(tree -> backend.generateOutput(tree, version))
                .thenAccept(result::complete);

        return result;
    }
}
