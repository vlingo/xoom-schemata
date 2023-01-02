// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

import io.vlingo.xoom.common.version.SemanticVersion;

public class SchemaVersionView extends View {
    private final String organizationId;
    private final String unitId;
    private final String contextId;
    private final String schemaId;
    private final String schemaVersionId;
    private final String description;
    private final String specification;
    private final String status;
    private final String previousVersion;
    private final String currentVersion;

    public static SchemaVersionView empty() {
        return new SchemaVersionView();
    }

    public static SchemaVersionView with(String schemaVersionId) {
        return new SchemaVersionView(schemaVersionId);
    }

    public static SchemaVersionView with(String organizationId, String unitId, String contextId, String schemaId, String schemaVersionId,
                                         String description, String specification, String status, String previousVersion, String currentVersion) {
        return new SchemaVersionView(organizationId, unitId, contextId, schemaId, schemaVersionId, description, specification, status,
                previousVersion, currentVersion);
    }

    protected SchemaVersionView() {
        this("", "", "", "", "", "", "", "", "", "");
    }

    private SchemaVersionView(String schemaVersionId) {
        this("", "", "", "", schemaVersionId, "", "", "", "", "");
    }

    protected SchemaVersionView(String organizationId, String unitId, String contextId, String schemaId, String schemaVersionId,
                             String description, String specification, String status, String previousVersion, String currentVersion) {
        this.organizationId = organizationId;
        this.unitId = unitId;
        this.contextId = contextId;
        this.schemaId = schemaId;
        this.schemaVersionId = schemaVersionId;
        this.description = description;
        this.specification = specification;
        this.status = status;
        this.previousVersion = previousVersion;
        this.currentVersion = currentVersion;
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

    public String schemaVersionId() {
        return schemaVersionId;
    }

    public String description() {
        return description;
    }

    public String specification() {
        return specification;
    }

    public String status() {
        return status;
    }

    public String previousVersion() {
        return previousVersion;
    }

    public String currentVersion() {
        return currentVersion;
    }

    public boolean hasCurrentVersion(String version) {
        return currentVersion.equals(version);
    }

    public int compareWith(SchemaVersionView view) {
        SemanticVersion sv1 = SemanticVersion.from(this.currentVersion());
        SemanticVersion sv2 = SemanticVersion.from(view.currentVersion());

        if (sv1.equals(sv2)) {
            return 0;
        } else if (sv2.isGreaterThan(sv1)) {
            return -1;
        } else {
            return 1;
        }
    }

    public boolean isNone() {
        return organizationId.isEmpty() && unitId.isEmpty() && contextId.isEmpty() && schemaId.isEmpty() && schemaVersionId.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + (schemaVersionId == null ? 0 : schemaVersionId.hashCode());
        result = prime * result + (description == null ? 0 : description.hashCode());
        result = prime * result + (specification == null ? 0 : specification.hashCode());
        result = prime * result + (status == null ? 0 : status.hashCode());
        result = prime * result + (previousVersion == null ? 0 : previousVersion.hashCode());
        result = prime * result + (currentVersion == null ? 0 : currentVersion.hashCode());
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

        return schemaVersionId.equals(((SchemaVersionView) other).schemaVersionId);
    }

    public SchemaVersionView mergeDescriptionWith(String schemaVersionId, String description) {
        if (this.schemaVersionId.equals(schemaVersionId)) {
            return new SchemaVersionView(this.schemaVersionId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    description, this.specification, this.status, this.previousVersion, this.currentVersion);
        } else {
            return this;
        }
    }

    public SchemaVersionView mergeVersionWith(String schemaVersionId, String version) {
        if (this.schemaVersionId.equals(schemaVersionId)) {
            return new SchemaVersionView(this.schemaVersionId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    description, this.specification, this.status, this.currentVersion, version);
        } else {
            return this;
        }
    }

    public SchemaVersionView mergeSpecificationWith(String schemaVersionId, String specification) {
        if (this.schemaVersionId.equals(schemaVersionId)) {
            return new SchemaVersionView(this.schemaVersionId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, specification, this.status, this.previousVersion, this.currentVersion);
        } else {
            return this;
        }
    }

    public SchemaVersionView mergeStatusWith(String schemaVersionId, String status) {
        if (this.schemaVersionId.equals(schemaVersionId)) {
            return new SchemaVersionView(this.schemaVersionId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, this.specification, status, this.previousVersion, this.currentVersion);
        } else {
            return this;
        }
    }

    public SchemaVersionView mergeWith(String schemaVersionId, String description, String specification, String status,
                                       String previousVersion, String currentVersion) {
        if (this.schemaVersionId.equals(schemaVersionId)) {
            return new SchemaVersionView(this.schemaVersionId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    description, specification, status, previousVersion, currentVersion);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "SchemaVersionView [organizationId=" + organizationId + ", unitId=" + unitId + ", contextId=" + contextId
                + ", schemaId=" + schemaId + ", schemaVersionId=" + schemaVersionId + ", description=" + description
                + ", specification=" + specification + ", status=" + status + ", previousVersion=" + previousVersion
                + ", currentVersion=" + currentVersion + "]";
    }
}
