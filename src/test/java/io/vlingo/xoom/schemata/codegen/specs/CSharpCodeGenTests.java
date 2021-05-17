// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.specs;

import io.vlingo.xoom.schemata.codegen.CodeGenTests;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CSharpCodeGenTests extends CodeGenTests {

  @Test
  public void testThatGeneratesDataClass() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("types/price"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public sealed class Price"));
    assertTrue(result.contains("public readonly double amount;"));
    assertTrue(result.contains("public readonly string currency;"));
    assertTrue(result.contains("public Price(double amount, string currency)"));
    assertTrue(result.contains("this.amount = amount;"));
    assertTrue(result.contains("this.currency = currency;"));
    assertFalse(result.contains("public Price()"));
  }

  @Test
  public void testThatGeneratesABasicType() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("using Vlingo.Lattice.Model;"));
    assertTrue(result.contains("public sealed class SalutationHappened : DomainEvent"));
    assertTrue(result.contains("public readonly string eventType;"));
    assertTrue(result.contains("public readonly long occurredOn;"));
    assertTrue(result.contains("public readonly int eventVersion;"));
    assertTrue(result.contains("public readonly string toWhom;"));
    assertTrue(result.contains("public readonly string text;"));
    assertTrue(result.contains("public SalutationHappened(string toWhom, string text)"));
    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(\"0.0.1\");"));
    assertTrue(result.contains("this.toWhom = toWhom;"));
    assertTrue(result.contains("this.text = text;"));
    assertFalse(result.contains("public SalutationHappened()"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValues() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public bool booleanAttribute = true;"));
    assertTrue(result.contains("public byte byteAttribute = 4;"));
    assertTrue(result.contains("public char charAttribute = 'x';"));
    assertTrue(result.contains("public double doubleAttribute = 0.23;"));
    assertTrue(result.contains("public float floatAttribute = 0.42f;"));
    assertTrue(result.contains("public int intAttribute = 4242;"));
    assertTrue(result.contains("public long longAttribute = 42L;"));
    assertTrue(result.contains("public short shortAttribute = 258;"));
    assertTrue(result.contains("public string stringAttribute = \"foo\";"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValuesAndComputedFields() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public SalutationHappened()"));

    assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
    assertTrue(result.contains("this.occurredOn = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(\"0.0.1\");"));

    assertTrue(result.contains("public bool booleanAttribute = true;"));
    assertTrue(result.contains("public byte byteAttribute = 4;"));
    assertTrue(result.contains("public char charAttribute = 'x';"));
    assertTrue(result.contains("public double doubleAttribute = 0.23;"));
    assertTrue(result.contains("public float floatAttribute = 0.42f;"));
    assertTrue(result.contains("public int intAttribute = 4242;"));
    assertTrue(result.contains("public long longAttribute = 42L;"));
    assertTrue(result.contains("public short shortAttribute = 258;"));
    assertTrue(result.contains("public string stringAttribute = \"foo\";"));
  }

  @Test
  public void testThatDefaultCtorIsOnlyAddedOnce() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("minimal"), fullyQualifiedTypeName, "0.0.1");

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
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basicArrays"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public readonly bool[] booleanAttribute"));
    assertTrue(result.contains("public readonly byte[] byteAttribute"));
    assertTrue(result.contains("public readonly char[] charAttribute"));
    assertTrue(result.contains("public readonly double[] doubleAttribute"));
    assertTrue(result.contains("public readonly float[] floatAttribute"));
    assertTrue(result.contains("public readonly int[] intAttribute"));
    assertTrue(result.contains("public readonly long[] longAttribute"));
    assertTrue(result.contains("public readonly short[] shortAttribute"));
    assertTrue(result.contains("public readonly string[] stringAttribute"));
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFieldsWithDefaults() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basicArraysWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public bool[] booleanAttribute = { true, false, true }"));
    assertTrue(result.contains("public byte[] byteAttribute = { 4, 3, 2, 1 }"));
    assertTrue(result.contains("public char[] charAttribute = { 'x', 'y', 'z' }"));
    assertTrue(result.contains("public double[] doubleAttribute = { 0.23, 0.22, 0.21 }"));
    assertTrue(result.contains("public float[] floatAttribute = { 0.42f, 0.42f, 0.1f }"));
    assertTrue(result.contains("public int[] intAttribute = { 4242, 424242, 42424242 }"));
    assertTrue(result.contains("public long[] longAttribute = { 42L, 4242L, 424242L }"));
    assertTrue(result.contains("public short[] shortAttribute = { 258, 259, 260 }"));
    assertTrue(result.contains("public string[] stringAttribute = { \"foo\", \"bar\", \"baz\" }"));
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("allSingleTypes"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("public readonly bool booleanAttribute;"));
    assertTrue(result.contains("public readonly byte byteAttribute;"));
    assertTrue(result.contains("public readonly char charAttribute;"));
    assertTrue(result.contains("public readonly double doubleAttribute;"));
    assertTrue(result.contains("public readonly float floatAttribute;"));
    assertTrue(result.contains("public readonly int intAttribute;"));
    assertTrue(result.contains("public readonly long longAttribute;"));
    assertTrue(result.contains("public readonly short shortAttribute;"));
    assertTrue(result.contains("public readonly string stringAttribute;"));
  }

  @Test
  public void testThatGeneratesAComposedTypeWithVersionedData() throws ExecutionException, InterruptedException, SchemataBusinessException {
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("price-changed"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1");

    assertTrue(result.contains("using Vlingo.Xoom.Common.Version;"));
    assertTrue(result.contains("using Vlingo.Lattice.Model;"));
    assertTrue(result.contains("public sealed class PriceChanged : DomainEvent"));
    assertTrue(result.contains("public readonly long occurredOn;"));
    assertTrue(result.contains("public readonly int eventVersion;"));
    assertTrue(result.contains("public readonly Price oldPrice;"));
    assertTrue(result.contains("public readonly Price newPrice;"));
    assertTrue(result.contains("public PriceChanged(Price oldPrice, Price newPrice)"));
    assertTrue(result.contains("this.occurredOn = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();"));
    assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(\"0.5.1\");"));
    assertTrue(result.contains("this.oldPrice = oldPrice;"));
    assertTrue(result.contains("this.newPrice = newPrice;"));
  }

  @Test
  public void testThatGeneratedClassIsInCorrectPackage() throws ExecutionException, InterruptedException {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.xoom.mynamespace:SalutationHappened";
    final String result = compileSpecAndUnwrap(generatorFor("csharp"),typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertTrue(result.contains("namespace io.vlingo.xoom.mynamespace.events"));
    assertTrue(result.contains("public sealed class SalutationHappened : DomainEvent"));
  }

  @Test
  public void testThatCompilingInvalidSchemaReportsError() {
    final Exception[] expected = new Exception[1];
    final boolean[] hitSuccess = { false };
      generatorFor("csharp")
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
