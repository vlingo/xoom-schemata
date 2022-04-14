// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.specs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.vlingo.xoom.schemata.codegen.CodeGenTests;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

abstract public class CodeGenSpecs extends CodeGenTests {

  @Test
  public void testThatGeneratesABasicType() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic");
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValues() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-with-default-values");
  }

  @Test
  public void testThatDefaultCtorIsOnlyAddedOnce() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("minimal"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "minimal");
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFields() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basicArrays"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-arrays");
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFieldsWithDefaults() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basicArraysWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-arrays-with-default-values");
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("allSingleTypes"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "all-single-types");
  }

  @Test
  public void testThatGeneratesAComposedTypeWithVersionedData() throws SchemataBusinessException {
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(typeDefinition("price-changed"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1");

    assertMatchesSpec(result, "price-changed");
  }

  @Test
  public void testThatGeneratesComplexTypeArrays() throws SchemataBusinessException {
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(typeDefinition("complexTypeArrays"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1");

    assertMatchesSpec(result, "complex-type-arrays");
  }

  @Test
  public void testThatGeneratedClassIsInCorrectPackage() {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.xoom.mynamespace:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-with-package");
  }

  @Test
  public void testThatCompilingInvalidSchemaReportsError() {
    final Exception[] expected = new Exception[1];
    final boolean[] hitSuccess = {false};
    compilerFor("java")
            .compile(typeDefinition("invalid"), "O:U:C:S", "0.0.1")
            .andThen(o -> o.resolve(
                    ex -> expected[0] = ex,
                    success -> hitSuccess[0] = true
            ))
            .await();

    assertNotNull("Parsing an invalid schema should report an exception", expected[0]);
    assertFalse("Parsing an invalid schema must not yield a successful result", hitSuccess[0]);
  }

  @Test
  public void testThatGeneratesTypeWithComputedOnlyFields() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("computedOnly"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "computed-only");
  }

  @Test
  public void testThatItGeneratesDataTypes() throws SchemataBusinessException {
    final String result = compileSpecAndUnwrap(typeDefinition("types/price"), "Org:Unit:Context:Schema:Price", "1.0.0");

    assertMatchesSpec(result, "types/price");
  }

  @Test
  public void testThatItGeneratesCommandTypes() throws SchemataBusinessException {
    registerType("types/price", "Vlingo:Xoom:io.vlingo.xoom.examples:Command:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(typeDefinition("command"), "Vlingo:Xoom:io.vlingo.xoom.examples:Command:ChangePrice", "1.0.0");

    assertMatchesSpec(result, "command");
  }

  @Test
  public void testThatItGeneratesEnvelopeTypes() throws SchemataBusinessException {
    registerType("types/price", "Vlingo:Xoom:io.vlingo.xoom.examples:Envelope:Price", "1.0.0");
    registerType("command", "Vlingo:Xoom:io.vlingo.xoom.examples:Envelope:ChangePrice", "1.0.0");
    final String result = compileSpecAndUnwrap(typeDefinition("envelope"), "Vlingo:Xoom:io.vlingo.xoom.examples:Envelope:IdentityEnvelope", "1.0.0");

    assertMatchesSpec(result, "envelope");
  }

  @Test
  public void testThatItGeneratesDocumentTypes() throws SchemataBusinessException {
    final String result = compileSpecAndUnwrap(typeDefinition("document"), "Vlingo:Xoom:io.vlingo.xoom.examples:Document:FlightInformation", "1.0.0");

    assertMatchesSpec(result, "document");
  }
}
