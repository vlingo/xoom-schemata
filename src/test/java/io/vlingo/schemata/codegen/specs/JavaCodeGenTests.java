package io.vlingo.schemata.codegen.specs;

import io.vlingo.common.version.SemanticVersion;
import io.vlingo.schemata.codegen.CodeGenTests;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.schemata.codegen.backends.java.JavaCodeGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class JavaCodeGenTests extends CodeGenTests {
    private TypeDefinitionCompiler compiler;

    @Before
    public void setUp() {
        compiler = TypeDefinitionCompiler.backedBy(new JavaCodeGenerator());
    }

    @Test
    public void testThatGeneratesABasicType() throws IOException {
        final String result = compiler.compile(typeDefinition("basic"));

        assertTrue(result.contains("import static io.vlingo.common.version.SemanticVersion.toValue;"));
        assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
        assertTrue(result.contains("public final String eventType;"));
        assertTrue(result.contains("public final Long occurredOn;"));
        assertTrue(result.contains("public final Integer eventVersion;"));
        assertTrue(result.contains("public final String toWhom;"));
        assertTrue(result.contains("public final String text;"));
        assertTrue(result.contains("public final SalutationHappened(final String toWhom, final String text) {"));
        assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
        assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
        assertTrue(result.contains("this.eventVersion = toValue(0, 0, 1);"));
        assertTrue(result.contains("this.toWhom = toWhom;"));
        assertTrue(result.contains("this.text = text;"));
    }

    @Test
    public void testThatGeneratesABasicTypeWithAllConsideredInnerTypes() throws IOException {
        final String result = compiler.compile(typeDefinition("allSingleTypes"));

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
