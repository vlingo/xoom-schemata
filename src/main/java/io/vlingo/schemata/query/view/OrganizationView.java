// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

public class OrganizationView {
  private String organizationId;
  private String name;
  private String description;

  public static OrganizationView empty() {
    return new OrganizationView();
  }

  public OrganizationView(final String organizationId, final String name, final String description) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }

  public OrganizationView(final String organizationId) {
    this(organizationId, "", "");
  }

  public OrganizationView() {
    this("", "", "");
  }

  public String organizationId() {
    return organizationId;
  }

  public void organizationId(final String organizationId) {
    this.organizationId = organizationId;
  }

  public String name() {
    return name;
  }

  public String name(final String name) {
    return this.name = name;
  }

  public String description() {
    return description;
  }

  public String description(final String description) {
    return this.description = description;
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

  public void initializeWith(String organizationId, String name, String description) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }

  public void mergeDescriptionWith(String organizationId, String description) {
    if (this.organizationId.equals(organizationId)) {
      this.description = description;
    }
  }

  public void mergeNameWith(String organizationId, String name) {
    if (this.organizationId.equals(organizationId)) {
      this.name = name;
    }
  }

  public void mergeWith(String organizationId, String name, String description) {
    if (this.description.equals(organizationId)) {
      this.name = name;
      this.description = description;
    }
  }

  @Override
  public String toString() {
    return "OrganizationView [organizationId=" + organizationId + ", name=" + name + ", description=" + description + "]";
  }
}
