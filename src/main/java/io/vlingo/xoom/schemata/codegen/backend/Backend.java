// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.backend;

import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.errors.SchemataBusinessException;

public interface Backend {
    Outcome<SchemataBusinessException,String> generateOutput(Node node, String version);
}
