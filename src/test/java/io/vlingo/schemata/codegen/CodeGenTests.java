// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public abstract class CodeGenTests {
  protected final String typeDefinition(final String name) {
    try {
      final InputStream resource = getClass().getResourceAsStream("/io/vlingo/schemata/codegen/vss/" + name + ".vss");
      return new BufferedReader(new InputStreamReader(resource)).lines().collect(Collectors.joining("\n"));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
