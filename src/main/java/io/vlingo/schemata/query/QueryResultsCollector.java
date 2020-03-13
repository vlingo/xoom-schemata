// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.schemata.resource.data.UnitData;

public interface QueryResultsCollector {
  Completes<Outcome<SchemataBusinessException,OrganizationData>> expectOrganization(final Completes<Outcome<SchemataBusinessException,OrganizationData>> organization);
  Completes<Outcome<SchemataBusinessException,UnitData>> expectUnit(final Completes<Outcome<SchemataBusinessException,UnitData>> unit);
  Completes<Outcome<SchemataBusinessException,ContextData>> expectContext(final Completes<Outcome<SchemataBusinessException,ContextData>> context);
  Completes<Outcome<SchemataBusinessException,SchemaData>> expectSchema(final Completes<Outcome<SchemataBusinessException,SchemaData>> schema);
  Completes<Outcome<SchemataBusinessException,SchemaVersionData>> expectSchemaVersion(final Completes<Outcome<SchemataBusinessException,SchemaVersionData>> version);
  Completes<Outcome<SchemataBusinessException,String>> expectCode(final Completes<Outcome<SchemataBusinessException,String>> code);

  OrganizationData organization();
  UnitData unit();
  ContextData context();
  SchemaData schema();
  SchemaVersionData schemaVersion();
  String code();
}
