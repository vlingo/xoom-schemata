// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import io.vlingo.schemata.codegen.antlr.SchemaVersionDefinitionParser;

public interface Backend {
  String generateFrom(final SchemaVersionDefinitionParser.TypeDeclarationContext typeDeclaration);
}
