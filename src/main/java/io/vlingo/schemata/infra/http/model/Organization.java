// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.http.model;

import java.util.List;

import io.vlingo.schemata.model.OrganizationEntity;

public class Organization {
  public final String id;
  public final String name;
  public final String description;
  public final List<Unit> units;

  private Organization(final String id, final String name, final String description, final List<Unit> units) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.units = units;
  }

  public static Organization from(final OrganizationEntity oe) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Organization from(final String id, final String name, final String description, final List<Unit> units) {
    return new Organization(id, name, description, units);
  }
}
