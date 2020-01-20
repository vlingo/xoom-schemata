// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

public class TypeDefinitionCompilerActor extends Actor implements TypeDefinitionCompiler, TypeDefinitionMiddleware {
    private final TypeParser parser;
    private final List<Processor> processors;
    private final Backend backend;
    private final TypeDefinitionMiddleware middleware;

    public TypeDefinitionCompilerActor(final TypeParser parser, final List<Processor> processors, final Backend backend) {
        this.parser = parser;
        this.processors = processors;
        this.backend = backend;
        this.middleware = selfAs(TypeDefinitionMiddleware.class);
    }

    @Override
    public Completes<String> compile(final InputStream typeDefinition, final String fullyQualifiedTypeName, final String version) {
        CompletesEventually eventually = completesEventually();

        parser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName)
                .andThenTo(this.process(fullyQualifiedTypeName))
                .andThenTo(tree -> backend.generateOutput(tree, version))
                .andThenConsume(eventually::with);

        return completes();
    }

    @Override
    public Completes<Node> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName) {
        CompletesEventually eventually = completesEventually();

        parser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName)
                .andThenTo(this.process(fullyQualifiedTypeName))
                .andThenConsume(eventually::with);

        return completes();
    }

    private Function<Node, Completes<Node>> process(final String fullyQualifiedTypeName) {
        return node -> {
            Completes<Node> result = Completes.withSuccess(node);
            for (Processor p : processors) {
                result = result.andThenTo(n -> p.process(n, middleware, fullyQualifiedTypeName));
            }

            return result;
        };
    }

    @Override
    public Completes<TypeDefinitionMiddleware> middleware() {
        CompletesEventually eventually = completesEventually();
        eventually.with(this.middleware);
        return completes();
    }
}
