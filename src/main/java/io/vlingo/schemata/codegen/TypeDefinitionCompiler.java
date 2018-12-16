package io.vlingo.schemata.codegen;

import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionLexer;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;
import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.nio.ByteBuffer;

public class TypeDefinitionCompiler {
    private final Backend backend;

    private TypeDefinitionCompiler(Backend backend) {
        this.backend = backend;
    }

    public static TypeDefinitionCompiler backedBy(final Backend backend) {
        return new TypeDefinitionCompiler(backend);
    }

    public String compile(final String typeDefinition) {
        CodePointBuffer buffer = CodePointBuffer.withBytes(ByteBuffer.wrap(typeDefinition.getBytes()));
        CodePointCharStream in = CodePointCharStream.fromBuffer(buffer);
        SchemaVersionDefinitionLexer lexer = new SchemaVersionDefinitionLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SchemaVersionDefinitionParser parser = new SchemaVersionDefinitionParser(tokens);

        return backend.generateFrom(parser.typeDeclaration());
    }
}
