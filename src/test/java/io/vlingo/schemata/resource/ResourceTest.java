// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import org.junit.Before;

import io.vlingo.actors.Grid;
import io.vlingo.actors.GridAddressFactory;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.ContextEntity;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.model.SchemaEntity;
import io.vlingo.schemata.model.SchemaVersionEntity;
import io.vlingo.schemata.model.UnitEntity;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.query.ContextQueries;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.query.SchemaQueries;
import io.vlingo.schemata.query.SchemaVersionQueries;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.symbio.store.DataFormat;
import io.vlingo.symbio.store.common.jdbc.hsqldb.HSQLDBConfigurationProvider;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public abstract class ResourceTest {
  protected Journal<String> journal;
  protected SourcedTypeRegistry registry;
  protected Stage stage;
  protected Grid grid;
  protected World world;

  protected OrganizationQueries organizationQueries;
  protected UnitQueries unitQueries;
  protected ContextQueries contextQueries;
  protected SchemaQueries schemaQueries;
  protected SchemaVersionQueries schemaVersionQueries;
  protected CodeQueries codeQueries;

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    // TODO: Start an actual Grid here using Grid.start(...). Needs a test grid configuration first
    world.stageNamed(Schemata.StageName, Stage.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));
    stage = world.stageNamed(Schemata.StageName);

    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, new NoopDispatcher());

    registry = new SourcedTypeRegistry(world);
    registry.register(new Info(journal, OrganizationEntity.class, OrganizationEntity.class.getSimpleName()));
    registry.register(new Info(journal, UnitEntity.class, UnitEntity.class.getSimpleName()));
    registry.register(new Info(journal, ContextEntity.class, ContextEntity.class.getSimpleName()));
    registry.register(new Info(journal, SchemaEntity.class, SchemaEntity.class.getSimpleName()));
    registry.register(new Info(journal, SchemaVersionEntity.class, SchemaVersionEntity.class.getSimpleName()));

    organizationQueries = Queries.forOrganizations();
    unitQueries = Queries.forUnits();
    contextQueries = Queries.forContexts();
    schemaQueries = Queries.forSchemas();
    schemaVersionQueries = Queries.forSchemaVersions();
    codeQueries = Queries.forCode();
  }

  protected JdbiOnDatabase jdbi() throws Exception {
    return JdbiOnDatabase.openUsing(
            HSQLDBConfigurationProvider.configuration(
                    DataFormat.Native,
                    "jdbc:hsqldb:mem:",
                    "vlingo_schemata",
                    "SA",
                    "",
                    "MAIN",
                    true));
  }

  protected String extractResourceIdFrom(final Response response) {
    final String[] parts = response.headerValueOr(ResponseHeader.Location, null).split("/");
    return parts[parts.length-1];
  }
}
