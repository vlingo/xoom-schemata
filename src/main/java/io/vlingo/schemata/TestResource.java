// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.Logger;
import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.http.Body;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

public class TestResource extends ResourceHandler {

  private final Stage stage;
  private final Logger logger;
  private final String content;

  public TestResource(final World world, String content) {
    this.stage = world.stageNamed("test-stage");
    this.logger = world.defaultLogger();
    this.content = content;
  }

  @Override
  public Resource<?> routes() {
    return resource("test", 10,
      get("/")
        .handle(() -> Completes.withSuccess(Response.of(Ok, Body.from(content)))));
  }
}
