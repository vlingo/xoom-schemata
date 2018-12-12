package io.vlingo.schemata.codegen.specs;

import io.vlingo.schemata.codegen.CodeGenTests;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.schemata.codegen.backends.JavaCodeGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class JavaCodeGenTests extends CodeGenTests {
    private TypeDefinitionCompiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = TypeDefinitionCompiler.backedBy(new JavaCodeGenerator());
    }

    @Test
    public void testThatGeneratesAnIntrinsicType() throws IOException {
        final String lol = typeDefinition("intrinsic");
        final String result = compiler.compile(lol);

        System.out.println(result);
        assertTrue(result.contains("public final class SalutationHappened extends DomainEvent {"));
        assertTrue(result.contains("public final String eventType;"));
        assertTrue(result.contains("public final Long occurredOn;"));
        assertTrue(result.contains("public final Integer eventVersion;"));
        assertTrue(result.contains("public final String toWhom;"));
        assertTrue(result.contains("public final String text;"));
        assertTrue(result.contains("public final SalutationHappened(String toWhom, String text) {"));
        assertTrue(result.contains("this.eventType = \"SalutationHappened\";"));
        assertTrue(result.contains("this.occurredOn = System.currentTimeMillis();"));
        assertTrue(result.contains("this.eventVersion = SemanticVersion.toValue(0, 0, 1);"));
        assertTrue(result.contains("this.toWhom = toWhom;"));
        assertTrue(result.contains("this.text = text;"));
    }
}
