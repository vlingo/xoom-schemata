package io.vlingo.schemata.codegen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class CodeGenTests {
    protected final String typeDefinition(final String name) {
        try {
            final InputStream resource = getClass().getResourceAsStream("/io/vlingo/schemata/codegen/vss/" + name + ".vss");
            return new BufferedReader(new InputStreamReader(resource))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
