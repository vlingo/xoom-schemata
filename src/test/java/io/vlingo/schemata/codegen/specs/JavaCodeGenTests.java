// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.specs;

import io.vlingo.schemata.codegen.CodeGenTests;
import io.vlingo.schemata.errors.SchemataBusinessException;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class JavaCodeGenTests extends CodeGenTests {
  @Test
  public void testThatGeneratesABasicType() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("import io.vlingo.lattice.model.DomainEvent;"));
    assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
    assertTrue(result.contains("public final String eventType;"));
    assertTrue(result.contains("public final long occurredOn;"));
    assertTrue(result.contains("public final int eventVersion;"));
    assertTrue(result.contains("public final String toWhom;"));
    assertTrue(result.contains("public final String text;"));
    assertTrue(result.contains("public SalutationHappened(final String toWhom, final String text) {"));
    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = io.vlingo.common.version.SemanticVersion.toValue(\"0.0.1\");"));
    assertTrue(result.contains("this.toWhom = toWhom;"));
    assertTrue(result.contains("this.text = text;"));
    assertFalse(result.contains("public SalutationHappened()"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValues() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public boolean booleanAttribute = true;"));
    assertTrue(result.contains("public byte byteAttribute = 4;"));
    assertTrue(result.contains("public char charAttribute = 'x';"));
    assertTrue(result.contains("public double doubleAttribute = 0.23;"));
    assertTrue(result.contains("public float floatAttribute = 0.42f;"));
    assertTrue(result.contains("public int intAttribute = 4242;"));
    assertTrue(result.contains("public long longAttribute = 42L;"));
    assertTrue(result.contains("public short shortAttribute = 258;"));
    assertTrue(result.contains("public String stringAttribute = \"foo\";"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValuesAndComputedFields() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public SalutationHappened()"));

    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = io.vlingo.common.version.SemanticVersion.toValue(\"0.0.1\");"));

    assertTrue(result.contains("public boolean booleanAttribute = true;"));
    assertTrue(result.contains("public byte byteAttribute = 4;"));
    assertTrue(result.contains("public char charAttribute = 'x';"));
    assertTrue(result.contains("public double doubleAttribute = 0.23;"));
    assertTrue(result.contains("public float floatAttribute = 0.42f;"));
    assertTrue(result.contains("public int intAttribute = 4242;"));
    assertTrue(result.contains("public long longAttribute = 42L;"));
    assertTrue(result.contains("public short shortAttribute = 258;"));
    assertTrue(result.contains("public String stringAttribute = \"foo\";"));
  }

  @Test
  public void testThatDefaultCtorIsOnlyAddedOnce() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("minimal"), fullyQualifiedTypeName, "0.0.1");

    Pattern pattern = Pattern.compile("public SalutationHappened\\(\\)");
    Matcher matcher = pattern.matcher(result);
    int matches = 0;
    while (matcher.find()) {
      matches++;
    }
    assertTrue(matches == 1);
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFields() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basicArrays"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public final boolean[] booleanAttribute"));
    assertTrue(result.contains("public final byte[] byteAttribute"));
    assertTrue(result.contains("public final char[] charAttribute"));
    assertTrue(result.contains("public final double[] doubleAttribute"));
    assertTrue(result.contains("public final float[] floatAttribute"));
    assertTrue(result.contains("public final int[] intAttribute"));
    assertTrue(result.contains("public final long[] longAttribute"));
    assertTrue(result.contains("public final short[] shortAttribute"));
    assertTrue(result.contains("public final String[] stringAttribute"));
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFieldsWithDefaults() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basicArraysWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public boolean[] booleanAttribute = new boolean[] { true, false, true }"));
    assertTrue(result.contains("public byte[] byteAttribute = new byte[] { 4, 3, 2, 1 }"));
    assertTrue(result.contains("public char[] charAttribute = new char[] { 'x', 'y', 'z' }"));
    assertTrue(result.contains("public double[] doubleAttribute = new double[] { 0.23, 0.22, 0.21 }"));
    assertTrue(result.contains("public float[] floatAttribute = new float[] { 0.42f, 0.42f, 0.1f }"));
    assertTrue(result.contains("public int[] intAttribute = new int[] { 4242, 424242, 42424242 }"));
    assertTrue(result.contains("public long[] longAttribute = new long[] { 42L, 4242L, 424242L }"));
    assertTrue(result.contains("public short[] shortAttribute = new short[] { 258, 259, 260 }"));
    assertTrue(result.contains("public String[] stringAttribute = new java.lang.String[] { \"foo\", \"bar\", \"baz\" }"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("allSingleTypes"), fullyQualifiedTypeName, "0.0.1");

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
  public void testThatGeneratesAComposedTypeWithVersionedData() throws ExecutionException, InterruptedException, SchemataBusinessException {
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("price-changed"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1");

    assertTrue(result.contains("public final class PriceChanged extends DomainEvent {"));
    assertTrue(result.contains("public final long occurredOn;"));
    assertTrue(result.contains("public final int eventVersion;"));
    assertTrue(result.contains("public final Price oldPrice;"));
    assertTrue(result.contains("public final Price newPrice;"));
    assertTrue(result.contains("public PriceChanged(final Price oldPrice, final Price newPrice) {"));
    assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
    assertTrue(result.contains("this.eventVersion = io.vlingo.common.version.SemanticVersion.toValue(\"0.5.1\");"));
    assertTrue(result.contains("this.oldPrice = oldPrice;"));
    assertTrue(result.contains("this.newPrice = newPrice;"));
  }

  @Test
  public void testThatGeneratedClassIsInCorrectPackage() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.mynamespace:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(),typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("package io.vlingo.mynamespace.event;"));
    assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
  }

  @Test
  public void testThatCompilingInvalidSchemaReportsError() {
    final Exception[] expected = new Exception[1];
    final boolean[] hitSuccess = { false };
      compilerWithJavaBackend()
            .compile(typeDefinition("invalid"), "O:U:C:S", "0.0.1")
            .andThen(o -> o.resolve(
                    ex -> expected[0] = ex,
                    success -> hitSuccess[0] = true
            ))
            .await();

    assertNotNull("Parsing an invalid schema should report an exception", expected[0]);
    assertFalse("Parsing an invalid schema must not yield a successful result", hitSuccess[0]);
  }
}
