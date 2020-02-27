// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.errors.EntityNotFoundException;
import io.vlingo.schemata.resource.data.OrganizationData;

public interface OrganizationQueries {
  Completes<List<OrganizationData>> organizations();
  Completes<Outcome<EntityNotFoundException, OrganizationData>> organization(final String organizationId);
  Completes<Outcome<EntityNotFoundException, OrganizationData>> organization(final String organizationId, final QueryResultsCollector collector);
  Completes<Outcome<EntityNotFoundException, OrganizationData>> organizationNamed(final String name);
  Completes<Outcome<EntityNotFoundException, OrganizationData>> organizationNamed(final String name, final QueryResultsCollector collector);
}
