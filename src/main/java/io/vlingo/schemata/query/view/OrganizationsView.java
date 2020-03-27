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

public class OrganizationsView {
  public static final String Id = "root";

  private List<Tag> organizations;

  public static OrganizationsView empty() {
    return new OrganizationsView();
  }

  public OrganizationsView() {
    this.organizations = new ArrayList<>();
  }

  public void add(final Tag organization) {
    if (!organizations.contains(organization)) {
      organizations.add(organization);
    }
  }

  public Tag get(final String organizationId) {
    Tag organization = Tag.only(organizationId);

    final int index = organizations.indexOf(organization);

    if (index >= 0) {
      organization = organizations.get(index);
    }

    return organization;
  }

  public void replace(final Tag organization) {
    final int index = organizations.indexOf(organization);
    if (index >= 0) {
      organizations.set(index, organization);
    }
  }

  public List<Tag> all() {
    return Collections.unmodifiableList(organizations);
  }

  @Override
  public String toString() {
    return "OrganizationsView [organizations=" + organizations + "]";
  }
}
