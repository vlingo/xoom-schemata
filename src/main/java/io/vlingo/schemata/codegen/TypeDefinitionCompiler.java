package io.vlingo.schemata.codegen;

import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionLexer;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class TypeDefinitionCompiler {
    private final Backend backend;

    private TypeDefinitionCompiler(Backend backend) {
        this.backend = backend;
    }

    public static TypeDefinitionCompiler backedBy(final Backend backend) {
        return new TypeDefinitionCompiler(backend);
    }

    public String compile(final String typeDefinition) throws IOException {
        ANTLRInputStream in = new ANTLRInputStream(new ByteArrayInputStream(typeDefinition.getBytes()));
        SchemaVersionDefinitionLexer lexer = new SchemaVersionDefinitionLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SchemaVersionDefinitionParser parser = new SchemaVersionDefinitionParser(tokens);

        return backend.generateFrom(parser);
    }
}
