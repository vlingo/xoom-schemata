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
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";

    final String result = compilerWithJavaBackend().compile(typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1").await(TIMEOUT);

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
    assertTrue(result.contains("this.eventVersion = SchemaVersion.Version.of(\"0.0.1\");"));
    assertTrue(result.contains("this.toWhom = toWhom;"));
    assertTrue(result.contains("this.text = text;"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";

    final String result = compilerWithJavaBackend().compile(typeDefinition("allSingleTypes"), fullyQualifiedTypeName, "0.0.1").await(TIMEOUT);

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
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compilerWithJavaBackend().compile(typeDefinition("price-changed"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1").await();

    assertTrue(result.contains("public final class PriceChanged extends DomainEvent {"));
    assertTrue(result.contains("public final long occurredOn;"));
    assertTrue(result.contains("public final SchemaVersion.Version eventVersion;"));
    assertTrue(result.contains("public final Price oldPrice;"));
    assertTrue(result.contains("public final Price newPrice;"));
    assertTrue(result.contains("public final PriceChanged(final Price oldPrice, final Price newPrice) {"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = SchemaVersion.Version.of(\"0.5.1\");"));
    assertTrue(result.contains("this.oldPrice = oldPrice;"));
    assertTrue(result.contains("this.newPrice = newPrice;"));
  }

  @Test
  public void testThatGeneratedClassIsInCorrectPackage() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.mynamespace:SalutationHappened";

    final String result = compilerWithJavaBackend().compile(typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1").await();

    assertTrue(result.contains("package io.vlingo.mynamespace.event;"));
    assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
  }
}
