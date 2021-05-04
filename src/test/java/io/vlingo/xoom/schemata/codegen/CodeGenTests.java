// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.TestWorld;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.codegen.ast.types.TypeDefinition;
import io.vlingo.xoom.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.codegen.processor.types.CacheTypeResolver;
import io.vlingo.xoom.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import org.junit.After;
import org.junit.Before;

import java.io.InputStream;
import java.util.Arrays;

public abstract class CodeGenTests {
    protected static final long TIMEOUT = 5000L;

    private World world;
    private CacheTypeResolver typeResolver;
    private TypeParser typeParser;

    @Before
    public void setUp() throws Exception {
        world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
        typeResolver = new CacheTypeResolver();
        typeParser = new AntlrTypeParser();
    }

    @After
    public void tearDown() throws Exception {
        world.terminate();
    }

    protected final TypeDefinitionCompiler compilerWithJavaBackend() {
        return new TypeDefinitionCompilerActor(
                typeParser,
                Arrays.asList(
                        world.actorFor(Processor.class, ComputableTypeProcessor.class),
                        world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
                ),
                "java"
        );
    }

    protected final void registerType(final String filePath, final String fullyQualifiedTypeName, final String version) {
      InputStream typeDefinition = typeDefinition(filePath);
        Outcome<SchemataBusinessException, Node> parsed = typeParser.parseTypeDefinition(typeDefinition, fullyQualifiedTypeName);

      typeResolver.produce((TypeDefinition) parsed.getOrNull(), version);
    }

    protected final InputStream typeDefinition(final String name) {
        try {
            return getClass().getResourceAsStream("/io/vlingo/xoom/schemata/codegen/vss/" + name + ".vss");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected String compileSpecAndUnwrap(
            TypeDefinitionCompiler typeDefinitionCompiler, InputStream spec,
            String fullyQualifiedTypeName, String version) {
        final Outcome<SchemataBusinessException, String> outcome = typeDefinitionCompiler
                .compile(
                        spec,
                        fullyQualifiedTypeName, version)
                .await(10000);

        return outcome.resolve(
                Throwable::getMessage,
                code -> code );
    }
}
