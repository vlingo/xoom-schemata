// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.OrganizationId;

public class OrganizationState {
  public final OrganizationId organizationId;
  public final String name;
  public final String description;

  public static OrganizationState from(final OrganizationId organizationId) {
    return new OrganizationState(organizationId);
  }

  public static OrganizationState from(final OrganizationId organizationId, final String name, final String description) {
    return new OrganizationState(organizationId, name, description);
  }

  public OrganizationState defineWith(final String name, final String description) {
    return new OrganizationState(this.organizationId, name, description);
  }

  public OrganizationState withDescription(final String description) {
    return new OrganizationState(this.organizationId, this.name, description);
  }

  public OrganizationState withName(final String name) {
    return new OrganizationState(this.organizationId, name, this.description);
  }

  public OrganizationState redefineWith(final String name, final String description) {
    return new OrganizationState(this.organizationId, name, description);
  }

  @Override
  public int hashCode() {
    return 31 * this.organizationId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final OrganizationState otherState = (OrganizationState) other;

    return this.organizationId.equals(otherState.organizationId);
  }

  @Override
  public String toString() {
    return "OrganizationState[organizationId=" + organizationId.value +
            " name=" + name +
            " description=" + description + "]";
  }

  private OrganizationState(final OrganizationId organizationId) {
    this(organizationId, "", "");
  }

  private OrganizationState(final OrganizationId organizationId, final String name, final String description) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }
}
