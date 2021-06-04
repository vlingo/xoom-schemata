package io.vlingo.xoom.schemata.codegen.specs;

import io.vlingo.xoom.schemata.codegen.CodeGenTests;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
  public void testThatGeneratedClassIsInCorrectPackage() {
    final String fullyQualifiedTypeName = "Org:Unit:io.vlingo.xoom.mynamespace:SalutationHappened";
    final String result = compileSpecAndUnwrap(typeDefinition("basic"), fullyQualifiedTypeName, "0.0.1");

    assertMatchesSpec(result, "basic-with-package");
  }

  @Test
  public void testThatCompilingInvalidSchemaReportsError() {
    final Exception[] expected = new Exception[1];
    final boolean[] hitSuccess = {false};
    ((TypeDefinitionCompiler) compilerFor("java"))
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
  public void testThatGeneratesTypeWithDependencies() {
    registerType("types/FullName", "xoom:apps:io.vlingo.xoom.examples.petclinic:FullName", "1.0.0");
    registerType("types/ContactInformation", "xoom:apps:io.vlingo.xoom.examples.petclinic:ContactInformation", "2.0.0");
    registerType("types/Speciality", "xoom:apps:io.vlingo.xoom.examples.petclinic:Speciality", "1.0.0");
    registerType("types/PostalAddress", "xoom:apps:io.vlingo.xoom.examples.petclinic:PostalAddress", "1.2.0");
    registerType("types/Telephone", "xoom:apps:io.vlingo.xoom.examples.petclinic:Telephone", "1.0.1");

    final String fullyQualifiedTypeName = "xoom:apps:io.vlingo.xoom.examples.petclinic:VeterinarianRegistered";
    final String result = compileSpecAndUnwrap(typeDefinition("veterinarian-registered"), fullyQualifiedTypeName, "1.0.0");

    assertMatchesSpec(result, "veterinarian-registered");
  }
}
