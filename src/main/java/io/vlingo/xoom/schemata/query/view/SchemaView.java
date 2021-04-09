// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Scope;

public class SchemaView {
    private final String organizationId;
    private final String unitId;
    private final String contextId;
    private final String schemaId;
    private final Category category;
    private final Scope scope;
    private final String name;
    private final String description;

    public static SchemaView empty() {
        return new SchemaView();
    }

    public static SchemaView with(String schemaId) {
        return new SchemaView(schemaId);
    }

    public static SchemaView with(String organizationId, String unitId, String contextId, String schemaId, Category category, Scope scope,
                                  String name, String description) {
        return new SchemaView(organizationId, unitId, contextId, schemaId, category, scope, name, description);
    }

    private SchemaView() {
        this("", "", "", "", Category.Unknown, Scope.Private, "", "");
    }

    private SchemaView(String schemaId) {
        this("", "", "", schemaId, Category.Unknown, Scope.Private, "", "");
    }

    private SchemaView(String organizationId, String unitId, String contextId, String schemaId, Category category, Scope scope,
                       String name, String description) {
        this.organizationId = organizationId;
        this.unitId = unitId;
        this.contextId = contextId;
        this.schemaId = schemaId;
        this.category = category;
        this.scope = scope;
        this.name = name;
        this.description = description;
    }

    public String organizationId() {
        return organizationId;
    }

    public String unitId() {
        return unitId;
    }

    public String contextId() {
        return contextId;
    }

    public String schemaId() {
        return schemaId;
    }

    public Category category() {
        return category;
    }

    public Scope scope() {
        return scope;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schemaId == null) ? 0 : description.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return schemaId.equals(((SchemaView) other).schemaId);
    }

    public SchemaView mergeCategoryWith(String schemaId, Category category) {
        if (this.schemaId.equals(schemaId)) {
            return new SchemaView(this.organizationId, this.unitId, this.contextId, this.schemaId, category, this.scope, this.name,
                    this.description);
        } else {
            return this;
        }
    }

    public SchemaView mergeScopeWith(String schemaId, Scope scope) {
        if (this.schemaId.equals(schemaId)) {
            return new SchemaView(this.organizationId, this.unitId, this.contextId, this.schemaId, this.category, scope, this.name,
                    this.description);
        } else {
            return this;
        }
    }

    public SchemaView mergeDescriptionWith(String schemaId, String description) {
        if (this.schemaId.equals(schemaId)) {
            return new SchemaView(this.organizationId, this.unitId, this.contextId, this.schemaId, this.category, this.scope, this.name,
                    description);
        } else {
            return this;
        }
    }

    public SchemaView mergeNameWith(String schemaId, String name) {
        if (this.schemaId.equals(schemaId)) {
            return new SchemaView(this.organizationId, this.unitId, this.contextId, this.schemaId, this.category, this.scope, name,
                    this.description);
        } else {
            return this;
        }
    }

    public SchemaView mergeWith(String schemaId, Category category, Scope scope, String name, String description) {
        if (this.schemaId.equals(schemaId)) {
            return new SchemaView(this.organizationId, this.unitId, this.contextId, this.schemaId, category, scope, name, description);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "SchemaView [organizationId=" + organizationId + ", unitId=" + unitId + ", contextId=" + contextId  + ", schemaId=" + schemaId
                + ", category=" + category + ", scope=" + scope + ", name=" + name + ", description=" + description + "]";
    }
}
