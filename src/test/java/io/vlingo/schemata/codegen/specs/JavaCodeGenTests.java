// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.specs;

import io.vlingo.schemata.codegen.CodeGenTests;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class JavaCodeGenTests extends CodeGenTests {
  @Test
  public void testThatGeneratesABasicType() throws ExecutionException, InterruptedException {
    final String result = compilerWithJavaBackend().compile(typeDefinition("basic"), "0.0.1").get();

    assertTrue(result.contains("import io.vlingo.lattice.model.DomainEvent;"));
    assertTrue(result.contains("import io.vlingo.schemata.model.SchemaVersion;"));
    assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
    assertTrue(result.contains("public final String eventType;"));
    assertTrue(result.contains("public final long occurredOn;"));
    assertTrue(result.contains("public final SchemaVersion.Version eventVersion;"));
    assertTrue(result.contains("public final String toWhom;"));
    assertTrue(result.contains("public final String text;"));
    assertTrue(result.contains("public final SalutationHappened(final String toWhom, final String text) {"));
    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.from(\"0.0.1\");"));
    assertTrue(result.contains("this.toWhom = toWhom;"));
    assertTrue(result.contains("this.text = text;"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws ExecutionException, InterruptedException {
    final String result = compilerWithJavaBackend().compile(typeDefinition("allSingleTypes"), "0.0.1").get();

    assertTrue(result.contains("public final boolean booleanAttribute;"));
    assertTrue(result.contains("public final byte byteAttribute;"));
    assertTrue(result.contains("public final char charAttribute;"));
    assertTrue(result.contains("public final double doubleAttribute;"));
    assertTrue(result.contains("public final float floatAttribute;"));
    assertTrue(result.contains("public final int intAttribute;"));
    assertTrue(result.contains("public final long longAttribute;"));
    assertTrue(result.contains("public final short shortAttribute;"));
    assertTrue(result.contains("public final String stringAttribute;"));
  }

  @Test
  public void testThatGeneratesAComposedTypeWithVersionedData() throws ExecutionException, InterruptedException {
    registerType("types/price", "1.0.0");
    final String result = compilerWithJavaBackend().compile(typeDefinition("price-changed"), "0.0.1").get();

    assertTrue(result.contains("public final class PriceChanged extends DomainEvent {"));
    assertTrue(result.contains("public final Long occurredOn;"));
    assertTrue(result.contains("public final Integer version;"));
    assertTrue(result.contains("public final Price newPrice;"));
    assertTrue(result.contains("public final PriceChanged(final Price newPrice) {"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(0, 0, 1);"));
    assertTrue(result.contains("this.newPrice = newPrice;"));
  }
}
