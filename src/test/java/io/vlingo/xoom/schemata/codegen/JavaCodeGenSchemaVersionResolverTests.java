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
import io.vlingo.xoom.schemata.SchemataConfig;
import io.vlingo.xoom.schemata.codegen.backend.java.JavaBackend;
import io.vlingo.xoom.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import io.vlingo.xoom.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.xoom.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.schemata.query.TypeResolverQueries;
import io.vlingo.xoom.schemata.query.TypeResolverQueriesActor;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class JavaCodeGenSchemaVersionResolverTests {
  @Test
  public void testThatSpecificationsContainingBasicTypesCanBeCompiledWithSchemaVersionQueryTypeResolver() throws Exception {
   final World world = TestWorld.startWithDefaults(getClass().getSimpleName()).world();
   final TypeParser typeParser = new AntlrTypeParser();
   final SchemataConfig config = SchemataConfig.forRuntime(SchemataConfig.RUNTIME_TYPE_DEV);
   final StateStoreProvider stateStoreProvider = StateStoreProvider.using(world, config);
   final ProjectionDispatcherProvider projectionDispatcherProvider = ProjectionDispatcherProvider.using(world.stage(), stateStoreProvider.stateStore);
   final StorageProvider storageProvider = StorageProvider.newInstance(world, stateStoreProvider.stateStore, projectionDispatcherProvider.storeDispatcher, config);
   final TypeResolver typeResolver = world.actorFor(TypeResolverQueries.class, TypeResolverQueriesActor.class, storageProvider.codeQueries);

   final TypeDefinitionCompiler typeDefinitionCompiler = new TypeDefinitionCompilerActor(
           typeParser,
           Arrays.asList(
                   world.actorFor(Processor.class, ComputableTypeProcessor.class),
                   world.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
           ),
           new JavaBackend()
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

   String result = compileSpecAndUnwrap(typeDefinitionCompiler, spec, "Org:Unit:Context:Schema:Foo:0.0.1", "0.0.1");

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

 private String compileSpecAndUnwrap(
         TypeDefinitionCompiler typeDefinitionCompiler, String spec,
         String fullyQualifiedTypeName, String version) {
   final Outcome<SchemataBusinessException, String> outcome = typeDefinitionCompiler
           .compile(
                   new ByteArrayInputStream(spec.getBytes()),
                   fullyQualifiedTypeName, version)
           .await(10000);

   return outcome.resolve(
           Throwable::getMessage,
           code -> code );
  }
}
