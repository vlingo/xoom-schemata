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
import io.vlingo.common.Outcome;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.errors.SchemataBusinessException;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

public class TypeDefinitionCompilerActor extends Actor implements TypeDefinitionCompiler, TypeDefinitionMiddleware {
    private final TypeParser parser;
    private final List<Processor> processors;
    private final Backend backend;

    public TypeDefinitionCompilerActor(final TypeParser parser, final List<Processor> processors, final Backend backend) {
        this.parser = parser;
        this.processors = processors;
        this.backend = backend;
    }

    @Override
    public Completes<Outcome<SchemataBusinessException, String>> compile(final InputStream typeDefinition, final String fullyQualifiedTypeName, final String version) {
        return Completes.withSuccess(
                parser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName)
                    .andThen(node -> this.process(fullyQualifiedTypeName).apply(node))
                    .andThenTo(node -> backend.generateOutput(node, version))
                    .otherwiseFail(ex -> ex)
                );
    }

    @Override
    public Completes<Outcome<SchemataBusinessException, Node>> compileToAST(final InputStream typeDefinition, final String fullyQualifiedTypeName) {
        return Completes.withSuccess(
                parser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName)
                        .andThen(this.process(fullyQualifiedTypeName))
                        .otherwiseFail(ex -> ex)
        );
    }

    private Function<Node, Node> process(final String fullyQualifiedTypeName) {
        return node -> {
            Completes<Node> result = Completes.withSuccess(node);
            for (Processor p : processors) {
                result = result.andThen(n -> p.process(n, this, fullyQualifiedTypeName)).await();
            }

            return result.await();
        };
    }

    @Override
    public TypeDefinitionMiddleware middleware() {
        return this;
    }
}
