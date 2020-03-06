// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.codegen.backend.Backend;
import io.vlingo.schemata.codegen.backend.java.JavaBackend;
import io.vlingo.schemata.codegen.parser.AntlrTypeParser;
import io.vlingo.schemata.codegen.parser.ParseException;
import io.vlingo.schemata.codegen.parser.TypeParser;
import io.vlingo.schemata.codegen.processor.Processor;
import io.vlingo.schemata.codegen.processor.types.ComputableTypeProcessor;
import io.vlingo.schemata.codegen.processor.types.TypeResolver;
import io.vlingo.schemata.codegen.processor.types.TypeResolverProcessor;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.query.Queries;

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
    case "java":
      return forBackend(stage, JavaBackend.class);
    default:
      throw new IllegalArgumentException("Unsupported language: " + language);
    }
  }

  /**
   * Answer a new {@code TypeDefinitionCompiler} for a given language {@code backendType}.
   * @param stage the Stage in which to create the compiler actor
   * @param backendType the {@code Class<? extends Actor>} of the language backend
   * @return TypeDefinitionCompiler
   */
  static TypeDefinitionCompiler forBackend(final Stage stage, final Class<? extends Actor> backendType) {
    final TypeParser typeParser = stage.actorFor(TypeParser.class, AntlrTypeParser.class);
    final TypeResolver typeResolver = Queries.forTypeResolver();

    return stage.actorFor(TypeDefinitionCompiler.class, TypeDefinitionCompilerActor.class,
            typeParser,
            Arrays.asList(
                    stage.actorFor(Processor.class, ComputableTypeProcessor.class),
                    stage.actorFor(Processor.class, TypeResolverProcessor.class, typeResolver)
            ),
            stage.actorFor(Backend.class, backendType)
    );
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
  Completes<TypeDefinitionMiddleware> middleware();


  // INTERNAL USE ONLY
  static class __TypeDefinitionCompiler__Holder {
    static final Map<String,TypeDefinitionCompiler> __internal__compilers = new ConcurrentHashMap<>();
  }
}
