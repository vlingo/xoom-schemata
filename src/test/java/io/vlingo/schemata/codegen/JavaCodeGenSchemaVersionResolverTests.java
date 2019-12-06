// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.codegen.CodeGenTests;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.schemata.codegen.TypeDefinitionCompilerActor;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.backend.java.JavaBackend;
import io.vlingo.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.schemata.infra.persistence.SchemataObjectStore;
import io.vlingo.schemata.query.SchemaVersionQueriesActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.ObjectStore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static io.vlingo.schemata.codegen.TypeDefinitionCompiler.compilerFor;
import static org.junit.Assert.assertTrue;

public class JavaCodeGenSchemaVersionResolverTests{
  @Test
  // Reproduces #98
  public void testThatSpecificationsContainingBasicTypesCanBeCompiledWithSchemaVersionQueryTypeResolver() throws Exception {
    World world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
    TypeParser typeParser = world.actorFor(TypeParser.class, AntlrTypeParser.class);
    Dispatcher dispatcher = new NoopDispatcher();

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance("dev");

    ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    TypeResolver typeResolver = new SchemaVersionQueriesActor(objectStore);

    TypeDefinitionCompiler typeDefinitionCompiler = world.actorFor(TypeDefinitionCompiler.class, TypeDefinitionCompilerActor.class,
            typeParser,
            Arrays.asList(
                    world.actorFor(Processor.class, ComputableTypeProcessor.class),
                    world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
            ),
            world.actorFor(Backend.class, JavaBackend.class, true)
    );
    String spec = "event Foo {\n" +
            "    version eventVersion\n" +
            "    string bar\n" +
            "}";

    final String result = typeDefinitionCompiler
            .compile(
                    new ByteArrayInputStream(spec.getBytes()),
                    "Org:Unit:Context:Schema:Foo:0.0.1", "0.0.1")
            .await();

    assertTrue(result.contains("public final class Foo extends DomainEvent {"));
    assertTrue(result.contains("public final SchemaVersion.Version eventVersion;"));
    assertTrue(result.contains("public final String bar;"));
  }
}