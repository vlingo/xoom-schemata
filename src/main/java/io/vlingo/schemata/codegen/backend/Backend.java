// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.backend;

import io.vlingo.common.Outcome;
import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.errors.SchemataBusinessException;

public interface Backend {
    Outcome<SchemataBusinessException,String> generateOutput(Node node, String version);
}
