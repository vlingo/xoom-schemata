// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.specs;

import io.vlingo.xoom.codegen.TextExpectation;
import io.vlingo.xoom.schemata.codegen.TypeDefinitionCompiler;

public class CSharpCodeGenTests extends CodeGenSpecs {
  @Override
  protected TypeDefinitionCompiler compiler() {
    return compilerFor("csharp");
  }

  @Override
  protected TextExpectation textExpectation() {
    return TextExpectation.onCSharp();
  }
}
