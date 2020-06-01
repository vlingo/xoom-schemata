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
    private final List<Tag> schemas;

    public static SchemasView empty() {
        return new SchemasView();
    }

    private SchemasView() {
        this.schemas = new ArrayList<>();
    }

    private SchemasView(List<Tag> units) {
        this.schemas = units;
    }

    public SchemasView add(final Tag unit) {
        if (schemas.contains(unit)) {
            return this;
        } else {
            SchemasView result = new SchemasView(new ArrayList<>(schemas));
            result.schemas.add(unit);

            return result;
        }
    }

    public Tag get(final String unitId) {
        Tag schema = Tag.only(unitId);

        final int index = schemas.indexOf(schema);

        if (index >= 0) {
            schema = schemas.get(index);
        }

        return schema;
    }

    public SchemasView replace(final Tag unit) {
        final int index = schemas.indexOf(unit);
        if (index >= 0) {
            SchemasView result = new SchemasView(new ArrayList<>(schemas));
            result.schemas.set(index, unit);

            return result;
        } else {
            return this;
        }
    }

    public List<Tag> all() {
        return Collections.unmodifiableList(schemas);
    }

    @Override
    public String toString() {
        return "SchemasView [schemas=" + schemas + "]";
    }
}
