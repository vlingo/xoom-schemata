// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import java.nio.ByteBuffer;

import org.antlr.v4.runtime.CodePointBuffer;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionLexer;
import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;

public class TypeDefinitionCompiler {
  private final Backend backend;

  private TypeDefinitionCompiler(final Backend backend) {
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
