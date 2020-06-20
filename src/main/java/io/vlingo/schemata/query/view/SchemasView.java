// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemasView {
    private final List<SchemaItem> schemas;

    public static SchemasView empty() {
        return new SchemasView();
    }

    private SchemasView() {
        this.schemas = new ArrayList<>();
    }

    private SchemasView(List<SchemaItem> units) {
        this.schemas = units;
    }

    public SchemasView add(final SchemaItem unit) {
        if (schemas.contains(unit)) {
            return this;
        } else {
            SchemasView result = new SchemasView(new ArrayList<>(schemas));
            result.schemas.add(unit);

            return result;
        }
    }

    public SchemaItem get(final String unitId) {
        SchemaItem schema = SchemaItem.only(unitId);

        final int index = schemas.indexOf(schema);

        if (index >= 0) {
            schema = schemas.get(index);
        }

        return schema;
    }

    public SchemasView replace(final SchemaItem unit) {
        final int index = schemas.indexOf(unit);
        if (index >= 0) {
            SchemasView result = new SchemasView(new ArrayList<>(schemas));
            result.schemas.set(index, unit);

            return result;
        } else {
            return this;
        }
    }

    public List<SchemaItem> all() {
        return Collections.unmodifiableList(schemas);
    }

    @Override
    public String toString() {
        return "SchemasView [schemas=" + schemas + "]";
    }

    public static class SchemaItem {
        public final String schemaId;
        public final String name;

        public static SchemaItem of(final String schemaId, final String name) {
            return new SchemaItem(schemaId, name);
        }

        public static SchemaItem only(final String schemaId) {
            return new SchemaItem(schemaId, "");
        }

        public SchemaItem(final String schemaId, final String name) {
            this.schemaId = schemaId;
            this.name = name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((schemaId == null) ? 0 : schemaId.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            return schemaId.equals(((SchemaItem) other).schemaId);
        }

        @Override
        public String toString() {
            return "SchemaItem [schemaId=" + schemaId + ", name=" + name + "]";
        }
    }
}
