// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.backend.Backend;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

public class TypeDefinitionCompilerActor implements TypeDefinitionCompiler, TypeDefinitionMiddleware {
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
                result = result.andThenTo(n -> p.process(n, this, fullyQualifiedTypeName));
            }

            return result.await();
        };
    }

    @Override
    public TypeDefinitionMiddleware middleware() {
        return this;
    }
}
