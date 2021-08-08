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

public class OrganizationsView extends View {
    public static final String Id = "root";

    private final List<OrganizationItem> organizations;

    public static OrganizationsView empty() {
        return new OrganizationsView();
    }

    private OrganizationsView() {
        this.organizations = new ArrayList<>();
    }

    public OrganizationsView(List<OrganizationItem> organizations) {
        this.organizations = organizations;
    }

    public OrganizationsView add(final OrganizationItem organization) {
        if (organizations.contains(organization)) {
            return this;
        } else {
            OrganizationsView result = new OrganizationsView(new ArrayList<>(organizations));
            result.organizations.add(organization);

            return result;
        }
    }

    public OrganizationItem get(final String organizationId) {
        OrganizationItem organization = OrganizationItem.only(organizationId);

        final int index = organizations.indexOf(organization);

        if (index >= 0) {
            organization = organizations.get(index);
        }

        return organization;
    }

    public OrganizationsView replace(final OrganizationItem organization) {
        final int index = organizations.indexOf(organization);
        if (index >= 0) {
            OrganizationsView result = new OrganizationsView(new ArrayList<>(organizations));
            result.organizations.set(index, organization);

            return result;
        } else {
            return this;
        }
    }

    public List<OrganizationItem> all() {
        return Collections.unmodifiableList(organizations);
    }

    @Override
    public String toString() {
        return "OrganizationsView [organizations=" + organizations + "]";
    }

    public static class OrganizationItem {
        public final String organizationId;
        public final String name;
        public final String description;

        public static OrganizationItem of(final String organizationId, final String name, final String description) {
            return new OrganizationItem(organizationId, name, description);
        }

        public static OrganizationItem of(final String organizationId, final String name) {
            return new OrganizationItem(organizationId, name, "");
        }

        public static OrganizationItem only(final String organizationId) {
            return new OrganizationItem(organizationId, "", "");
        }

        public OrganizationItem(final String organizationId, final String name, final String description) {
            this.organizationId = organizationId;
            this.name = name;
            this.description = description;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
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

            return organizationId.equals(((OrganizationItem) other).organizationId);
        }

        @Override
        public String toString() {
            return "OrganizationItem [organizationId=" + organizationId + ", name=" + name + "]";
        }
    }
}
