// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

public class OrganizationView {
    private final String organizationId;
    private final String name;
    private final String description;

    public static OrganizationView empty() {
        return new OrganizationView();
    }

    public static OrganizationView with(final String organizationId) {
        return new OrganizationView(organizationId);
    }

    public static OrganizationView with(String organizationId, String name, String description) {
        return new OrganizationView(organizationId, name, description);
    }

    private OrganizationView(final String organizationId, final String name, final String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
    }

    private OrganizationView(final String organizationId) {
        this(organizationId, "", "");
    }

    private OrganizationView() {
        this("", "", "");
    }

    public String organizationId() {
        return organizationId;
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
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
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

        return organizationId.equals(((OrganizationView) other).organizationId);
    }

    public OrganizationView mergeDescriptionWith(String organizationId, String description) {
        if (this.organizationId.equals(organizationId)) {
            return new OrganizationView(organizationId, this.name, description);
        } else {
            return this;
        }
    }

    public OrganizationView mergeNameWith(String organizationId, String name) {
        if (this.organizationId.equals(organizationId)) {
            return new OrganizationView(organizationId, name, this.description);
        } else {
            return this;
        }
    }

    public OrganizationView mergeWith(String organizationId, String name, String description) {
        if (this.description.equals(organizationId)) {
            return new OrganizationView(organizationId, name, description);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "OrganizationView [organizationId=" + organizationId + ", name=" + name + ", description=" + description + "]";
    }
}
