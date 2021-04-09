// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.parser;

import java.io.InputStream;

import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

public interface TypeParser {
    Outcome<SchemataBusinessException,Node> parseTypeDefinition(final InputStream inputStream, final String fullyQualifiedTypeName);
}
