// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

public class ContextReference {
  public final String organizationId;
  public final String unitId;
  public final String contextId;

  public static ContextReference identity(final String organizationId, final String unitId, final String contextId) {
    return new ContextReference(organizationId, unitId, contextId);
  }

  private ContextReference(final String organizationId, final String unitId, final String contextId) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
  }
}
