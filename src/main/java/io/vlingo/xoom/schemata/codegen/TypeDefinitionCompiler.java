// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.backend.Backend;
import io.vlingo.xoom.schemata.codegen.backend.XoomCodeGenBackend;
import io.vlingo.xoom.schemata.codegen.template.schematype.SchemaTypeTemplateProcessingStep;
import io.vlingo.xoom.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.xoom.schemata.codegen.parser.TypeParser;
import io.vlingo.xoom.schemata.codegen.processor.Processor;
import io.vlingo.xoom.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.xoom.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A compiler of schema types, producing source code for a given language.
 */
public interface TypeDefinitionCompiler {
  /**
   * Answer a cached {@code TypeDefinitionCompiler} for a given {@code language},
   * or a new one if one is not currently cached.
   * @param stage the Stage in which to create the compiler actor
   * @param language the String identifying the language
   * @return TypeDefinitionCompiler
   */
  public static TypeDefinitionCompiler compilerFor(final Stage stage, final String language) {
    return __TypeDefinitionCompiler__Holder.__internal__compilers.computeIfAbsent(
            language,
            (key) -> TypeDefinitionCompiler.newCompilerFor(stage, language));
  }

  /**
   * Answer a new {@code TypeDefinitionCompiler} for a given {@code language}.
   * @param stage the Stage in which to create the compiler actor
   * @param language the String identifying the language
   * @return TypeDefinitionCompiler
   */
  public static TypeDefinitionCompiler newCompilerFor(final Stage stage, final String language) {
    switch (language) {
    case "csharp":
    case "java":
      return forBackend(stage, new XoomCodeGenBackend(new SchemaTypeTemplateProcessingStep(), language));
    default:
      throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }

  /**
   * Answer a new {@code TypeDefinitionCompiler} for a given language {@code backendType}.
   * @param stage the Stage in which to create the compiler actor
   * @param backend the language backend
   * @return TypeDefinitionCompiler
   */
  static TypeDefinitionCompiler forBackend(final Stage stage, Backend backend) {
    final TypeParser typeParser =  new AntlrTypeParser();
    final TypeResolver typeResolver = StorageProvider.instance().typeResolverQueries;

    return new TypeDefinitionCompilerActor(typeParser,
            Arrays.asList(
                    stage.actorFor(Processor.class, ComputableTypeProcessor.class),
                    stage.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
            ), backend);
  }

  /**
   * Answer the eventually generated source code given the {@code typeDefinition} and {@code version}.
   * @param typeDefinition the InputStream providing the schema type definition
   * @param fullyQualifiedTypeName the String FQTN of the type to be compiled
   * @param version the String version of the definition
   * @return {@code Completes<String>}
   */
  Completes<Outcome<SchemataBusinessException,String>> compile(final InputStream typeDefinition, final String fullyQualifiedTypeName, final String version);

  /**
   * Answer this compiler's middleware.
   * @return {@code TypeDefinitionMiddleware}
   */
  TypeDefinitionMiddleware middleware();


  // INTERNAL USE ONLY
  static class __TypeDefinitionCompiler__Holder {
    static final Map<String,TypeDefinitionCompiler> __internal__compilers = new ConcurrentHashMap<>();
  }
}
