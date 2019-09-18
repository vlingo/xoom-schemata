// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.parser;

import java.io.InputStream;

import io.vlingo.common.Completes;
import io.vlingo.schemata.codegen.ast.Node;

public interface TypeParser {
    Completes<Node> parseTypeDefinition(final InputStream inputStream, final String fullyQualifiedTypeName);
}
