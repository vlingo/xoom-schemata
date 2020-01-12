// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.schemata.resource.data.UnitData;

public interface QueryResultsCollector {
  Completes<OrganizationData> expectOrganization(final Completes<OrganizationData> organization);
  Completes<UnitData> expectUnit(final Completes<UnitData> unit);
  Completes<ContextData> expectContext(final Completes<ContextData> context);
  Completes<SchemaData> expectSchema(final Completes<SchemaData> schema);
  Completes<SchemaVersionData> expectSchemaVersion(final Completes<SchemaVersionData> version);
  Completes<String> expectCode(final Completes<String> code);

  OrganizationData organization();
  UnitData unit();
  ContextData context();
  SchemaData schema();
  SchemaVersionData schemaVersion();
  String code();
}
