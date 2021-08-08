// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemaVersionsView extends View {
    private final List<SchemaVersionView> schemaVersions;

    public static SchemaVersionsView empty() {
        return new SchemaVersionsView();
    }

    public static SchemaVersionsView with(List<SchemaVersionView> schemaVersions) {
        return new SchemaVersionsView(schemaVersions);
    }

    private SchemaVersionsView() {
        this.schemaVersions = new ArrayList<>();
    }

    private SchemaVersionsView(List<SchemaVersionView> schemaVersions) {
        this.schemaVersions = schemaVersions;
    }

    public SchemaVersionsView add(final SchemaVersionView view) {
        if (schemaVersions.contains(view)) {
            return this;
        } else {
            SchemaVersionsView result = new SchemaVersionsView(new ArrayList<>(schemaVersions));
            result.schemaVersions.add(view);

            return result;
        }
    }

    public SchemaVersionView get(final String schemaVersionId) {
        SchemaVersionView view = SchemaVersionView.with(schemaVersionId);
        final int index = schemaVersions.indexOf(view);

        return index >= 0
                ? schemaVersions.get(index)
                : view;
    }

    public SchemaVersionView greatestVersion() {
        return schemaVersions.stream()
                .max(SchemaVersionView::compareWith)
                .orElse(SchemaVersionView.empty());
    }

    public SchemaVersionView withVersion(String version) {
        return schemaVersions.stream()
                .filter(view -> view.hasCurrentVersion(version))
                .findAny()
                .orElse(SchemaVersionView.empty());
    }

    public SchemaVersionsView mergeDescriptionWith(String schemaVersionId, String description) {
        final int index = schemaVersions.indexOf(SchemaVersionView.with(schemaVersionId));
        if (index >= 0) {
            SchemaVersionView updated = schemaVersions.get(index)
                    .mergeDescriptionWith(schemaVersionId, description);
            SchemaVersionsView result = new SchemaVersionsView(new ArrayList<>(schemaVersions));
            result.schemaVersions.set(index, updated);

            return result;
        } else {
            return this;
        }
    }

    public SchemaVersionsView mergeSpecificationWith(String schemaVersionId, String specification) {
        final int index = schemaVersions.indexOf(SchemaVersionView.with(schemaVersionId));
        if (index >= 0) {
            SchemaVersionView updated = schemaVersions.get(index)
                    .mergeSpecificationWith(schemaVersionId, specification);
            SchemaVersionsView result = new SchemaVersionsView(new ArrayList<>(schemaVersions));
            result.schemaVersions.set(index, updated);

            return result;
        } else {
            return this;
        }
    }

    public SchemaVersionsView mergeVersionWith(String schemaVersionId, String version) {
        final int index = schemaVersions.indexOf(SchemaVersionView.with(schemaVersionId));
        if (index >= 0) {
            SchemaVersionView updated = schemaVersions.get(index)
                    .mergeVersionWith(schemaVersionId, version);
            SchemaVersionsView result = new SchemaVersionsView(new ArrayList<>(schemaVersions));
            result.schemaVersions.set(index, updated);

            return result;
        } else {
            return this;
        }
    }

    public SchemaVersionsView mergeStatusWith(String schemaVersionId, String status) {
        final int index = schemaVersions.indexOf(SchemaVersionView.with(schemaVersionId));
        if (index >= 0) {
            SchemaVersionView updated = schemaVersions.get(index)
                    .mergeStatusWith(schemaVersionId, status);
            SchemaVersionsView result = new SchemaVersionsView(new ArrayList<>(schemaVersions));
            result.schemaVersions.set(index, updated);

            return result;
        } else {
            return this;
        }
    }

    public List<SchemaVersionView> all() {
        return Collections.unmodifiableList(schemaVersions);
    }

    @Override
    public String toString() {
        return "SchemaVersionsView [views=" + schemaVersions + "]";
    }
}
