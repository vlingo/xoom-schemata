// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.Map;

import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.symbio.store.object.MapQueryExpression.FluentMap;
import io.vlingo.symbio.store.object.StateObject;

public class OrganizationState extends StateObject {
  private static final long serialVersionUID = 1L;

  public final OrganizationId organizationId;
  public final String name;
  public final String description;

  public static OrganizationState from(final OrganizationId organizationId) {
    return new OrganizationState(organizationId);
  }

  public static OrganizationState from(final long id, final long version, final OrganizationId organizationId, final String name, final String description) {
    return new OrganizationState(id, version, organizationId, name, description);
  }

  public OrganizationState defineWith(final String name, final String description) {
    return new OrganizationState(this.persistenceId(), this.version() + 1, this.organizationId, name, description);
  }

  public OrganizationState withDescription(final String description) {
    return new OrganizationState(this.persistenceId(), this.version() + 1, this.organizationId, this.name, description);
  }

  public OrganizationState withName(final String name) {
    return new OrganizationState(this.persistenceId(), this.version() + 1, this.organizationId, name, this.description);
  }

  @Override
  public Map<String, Object> queryMap() {
    return FluentMap.has("organizationId", organizationId.value);
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

    return this.persistenceId() == otherState.persistenceId();
  }

  @Override
  public String toString() {
    return "OrganizationState[persistenceId=" + persistenceId() +
            " version=" + version() +
            " organizationId=" + organizationId.value +
            " name=" + name +
            " description=" + description + "]";
  }

  private OrganizationState(final OrganizationId organizationId) {
    this(Unidentified, 0, organizationId, "", "");
  }

  private OrganizationState(final long id, final long version, final OrganizationId organizationId, final String name, final String description) {
    super(id, version);
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }
}
