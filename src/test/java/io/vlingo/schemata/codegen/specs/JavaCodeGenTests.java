// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.specs;

import io.vlingo.schemata.codegen.CodeGenTests;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class JavaCodeGenTests extends CodeGenTests {
  @Test
  public void testThatGeneratesABasicType() throws ExecutionException, InterruptedException {
    final String result = compilerWithJavaBackend().compile(typeDefinition("basic")).get();

    assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
    assertTrue(result.contains("public final String eventType;"));
    assertTrue(result.contains("public final Long occurredOn;"));
    assertTrue(result.contains("public final Integer eventVersion;"));
    assertTrue(result.contains("public final String toWhom;"));
    assertTrue(result.contains("public final String text;"));
    assertTrue(result.contains("public final SalutationHappened(final String toWhom, final String text) {"));
    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(0, 0, 1);"));
    assertTrue(result.contains("this.toWhom = toWhom;"));
    assertTrue(result.contains("this.text = text;"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws ExecutionException, InterruptedException {
    final String result = compilerWithJavaBackend().compile(typeDefinition("allSingleTypes")).get();

    assertTrue(result.contains("public final Boolean booleanAttribute;"));
    assertTrue(result.contains("public final Byte byteAttribute;"));
    assertTrue(result.contains("public final Character charAttribute;"));
    assertTrue(result.contains("public final Double doubleAttribute;"));
    assertTrue(result.contains("public final Float floatAttribute;"));
    assertTrue(result.contains("public final Integer intAttribute;"));
    assertTrue(result.contains("public final Long longAttribute;"));
    assertTrue(result.contains("public final Short shortAttribute;"));
    assertTrue(result.contains("public final String stringAttribute;"));
  }
}
