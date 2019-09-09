// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.identity.IdentityGeneratorType;
import io.vlingo.http.Context;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.RequestHeader;
import io.vlingo.http.Response;
import io.vlingo.lattice.grid.Grid;
import io.vlingo.lattice.grid.GridAddressFactory;
import io.vlingo.lattice.model.object.ObjectTypeRegistry;
import io.vlingo.lattice.model.object.ObjectTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.model.Unit;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.query.Queries;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.StateObjectMapper;
import io.vlingo.symbio.store.object.inmemory.InMemoryObjectStoreActor;

public class CodeResourceTest {
  private ObjectStore objectStore;
  private CodeQueries queries;
  private ObjectTypeRegistry registry;
  private Stage stage;
  private World world;

  @Test
  public void testThatCodeIsReferenced() {
    final CodeResource resource = new CodeResource(world, queries);
    resource.__internal__test_set_up(context(), stage);
    final Response response = resource.queryCodeForLanguage("O1:U1:C1:S1:1.0.0", "java").await();
    assertEquals(Ok, response.status);
    assertTrue(response.entity.content().contains("ItHappened"));
    assertTrue(response.entity.content().equals("event ItHappened {}"));
  }

  @Before
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void setUp() throws Exception {
    world = World.startWithDefaults("test-command-router");
    world.stageNamed(Schemata.StageName, Grid.class, new GridAddressFactory(IdentityGeneratorType.RANDOM));
    stage = world.stageNamed(Schemata.StageName);

    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, new NoopDispatcher());

    queries = Queries.forCode(world.stageNamed(Schemata.StageName), objectStore);

    registry = new ObjectTypeRegistry(world);

    final Info<Unit> unitInfo =
            new Info(
                  objectStore,
                  OrganizationState.class,
                  "Organization",
                  MapQueryExpression.using(Organization.class, "find", MapQueryExpression.map("id", "id")),
                  StateObjectMapper.with(Organization.class, new Object(), new Object()));

    registry.register(unitInfo);
  }

  private Context context() {
    final String authValue = AuthorizationData.AuthorizationType + " source=1234567890 dependent=0987654321";

    final Request request =
            Request
              .has(Method.GET)
              .and(RequestHeader.of(RequestHeader.Authorization, authValue));

    return new Context(request, (CompletesEventually) null);
  }
}
