// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.specs;

import io.vlingo.xoom.codegen.TextExpectation;
import io.vlingo.xoom.schemata.codegen.CodeGenTests;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class JavaCodeGenTests extends CodeGenTests {
  @Test
  public void testThatGeneratesABasicType() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic");
  }

  @Test
  public void testThatGeneratesABasicTypeWithDefaultValues() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("basicWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-with-default-values");
  }

  @Test
  public void testThatDefaultCtorIsOnlyAddedOnce() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("minimal"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "minimal");
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFields() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("basicArrays"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-arrays");
  }

  @Test
  public void testThatGeneratesBasicTypeArrayFieldsWithDefaults() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("basicArraysWithDefaultValues"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-arrays-with-default-values");
  }

  @Test
  public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("allSingleTypes"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "all-single-types");
  }

  @Test
  public void testThatGeneratesAComposedTypeWithVersionedData() throws SchemataBusinessException {
    registerType("types/price", "Org:Unit:Context:Schema:Price", "1.0.0");
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("price-changed"), "Org:Unit:Context:Schema:PriceChanged", "0.5.1");

    assertMatchesSpec(result, "price-changed");
  }

  @Test
  public void testThatGeneratedClassIsInCorrectPackage() {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.xoom.mynamespace:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-with-package");
  }

  @Test
  public void testThatCompilingInvalidSchemaReportsError() {
    final Exception[] expected = new Exception[1];
    final boolean[] hitSuccess = {false};
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

  @Test
  public void testThatGeneratesTypeWithComputedOnlyFields() {
    final String fullyQualifiedTypeName = "Org:Unit:Context:Schema:SalutationHappened";
    final String result = compileSpecAndUnwrap(compilerWithJavaBackend(), typeDefinition("computedOnly"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "computed-only");
  }

  @Override
  protected TextExpectation textExpectation() {
    return TextExpectation.onJava();
  }
}
