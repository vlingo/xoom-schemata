// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.SchemataConfig;
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
import io.vlingo.schemata.query.TypeResolverQueriesActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.object.ObjectStore;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JavaCodeGenSchemaVersionResolverTests{
  @Test
  public void testThatSpecificationsContainingBasicTypesCanBeCompiledWithSchemaVersionQueryTypeResolver() throws Exception {
    World world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
    TypeParser typeParser = world.actorFor(TypeParser.class, AntlrTypeParser.class);
    Dispatcher dispatcher = new NoopDispatcher();

    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(SchemataConfig.forRuntime("test"));

    ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
    schemataObjectStore.register(registry, objectStore);

    TypeResolver typeResolver = new TypeResolverQueriesActor(new SchemaVersionQueriesActor(objectStore));

    TypeDefinitionCompiler typeDefinitionCompiler = world.actorFor(TypeDefinitionCompiler.class, TypeDefinitionCompilerActor.class,
            typeParser,
            Arrays.asList(
                    world.actorFor(Processor.class, ComputableTypeProcessor.class),
                    world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
            ),
            world.actorFor(Backend.class, JavaBackend.class)
    );
    String spec = "event Foo {\n" +
            "    type eventType\n" +
            "    version eventVersion\n" +
            "    timestamp occurredOn\n" +
            "\n" +
            "    boolean booleanAttribute\n"+
            "    byte byteAttribute\n"+
            "    char charAttribute\n"+
            "    double doubleAttribute\n"+
            "    float floatAttribute\n"+
            "    int intAttribute\n"+
            "    long longAttribute\n"+
            "    short shortAttribute\n"+
            "    string stringAttribute\n"+
            "}";

    final String result = typeDefinitionCompiler
            .compile(
                    new ByteArrayInputStream(spec.getBytes()),
                    "Org:Unit:Context:Schema:Foo:0.0.1", "0.0.1")
            .await();

    assertTrue(result.contains("public final class Foo extends DomainEvent {"));
    assertTrue(result.contains("public final String eventType;"));
    assertTrue(result.contains("public final int eventVersion;"));
    assertTrue(result.contains("public final long occurredOn;"));

    assertTrue(result.contains("public final boolean booleanAttribute;"));
    assertTrue(result.contains("public final byte byteAttribute;"));
    assertTrue(result.contains("public final char charAttribute;"));
    assertTrue(result.contains("public final double doubleAttribute;"));
    assertTrue(result.contains("public final float floatAttribute;"));
    assertTrue(result.contains("public final int intAttribute;"));
    assertTrue(result.contains("public final long longAttribute;"));
    assertTrue(result.contains("public final short shortAttribute;"));
    assertTrue(result.contains("public final String stringAttribute;"));  }
}
