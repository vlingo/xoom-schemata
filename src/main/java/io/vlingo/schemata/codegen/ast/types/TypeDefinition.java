// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.codegen.ast.types;

import java.util.List;

import io.vlingo.schemata.codegen.ast.Node;
import io.vlingo.schemata.model.Category;

public class TypeDefinition implements Node, Type {
    public final Category category;
    public final String fullyQualifiedTypeName;
    public final String typeName;
    public final List<Node> children;

    public TypeDefinition(Category category, final String fullyQualifiedTypeName, String typeName, List<Node> children) {
        this.category = category;
        this.fullyQualifiedTypeName = fullyQualifiedTypeName;
        this.typeName = typeName;
        this.children = children;
    }
}
