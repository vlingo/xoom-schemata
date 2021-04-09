// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.ast.types;

import java.util.List;
import java.util.Objects;

import io.vlingo.xoom.schemata.codegen.ast.Node;
import io.vlingo.xoom.schemata.model.Category;

public class TypeDefinition implements Type {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefinition that = (TypeDefinition) o;
        return category == that.category &&
          Objects.equals(fullyQualifiedTypeName, that.fullyQualifiedTypeName) &&
          Objects.equals(typeName, that.typeName) &&
          Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, fullyQualifiedTypeName, typeName, children);
    }

    @Override
    public String name() {
        return typeName;
    }
}
