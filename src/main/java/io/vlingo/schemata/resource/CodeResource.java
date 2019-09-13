// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.RequestHeader.Authorization;
import static io.vlingo.http.Response.Status.BadRequest;
import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.common.Tuple2;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.schemata.query.CodeQueries;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.PathData;

//
// like this:
//    /code/Org:Unit:Context:Schema:Version/java
//    /code/Org:Unit:Context:Schema:Version/csharp
//    /code/Org:Unit:Context:Schema:Version/ruby
//    /code/Org:Unit:Context:Schema:Version/python
//    /code/Org:Unit:Context:Schema:Version/???
//
// header: Authorization: VLINGO-SCHEMATA source=<some-hash-value> dependent=<some-hash-value>
//
public class CodeResource extends ResourceHandler {
  private final CodeQueries queries;

  public CodeResource(final World world, final CodeQueries queries) {
    this.queries = queries;
  }

  public Completes<Response> queryCodeForLanguage(final String reference, final String language) {
    return queryParameters(context().request, reference)
              .resolve(failure -> Completes.withSuccess(Response.of(BadRequest, failure.getMessage())),
                       success ->
                         queries.schemaVersionFor(success._1, success._2)
                                .andThen(version -> {
                                   Response r1 = Response.of(Ok, version.specification);
                                   return r1;
                                 }));
  }

  @Override
  public Resource<?> routes() {
    return resource("Code Resource",
      get("/code/{reference}/{language}")
        .param(String.class)
        .param(String.class));
  }

  private Outcome<IllegalArgumentException, Tuple2<AuthorizationData, PathData>> queryParameters(final Request request, final String reference) {
    AuthorizationData auth = null;
    PathData path = null;
    final String header = request.headerValueOr(Authorization, "");

    try {
      auth = AuthorizationData.with(header);
      path = PathData.from(reference);
      return Success.of(Tuple2.from(auth, path));
    } catch (Exception e) {
      if (auth == null) return Failure.of(new IllegalArgumentException("Authorization is invalid: " + header));
      return Failure.of(new IllegalArgumentException("Reference path is invalid: " + reference));
    }
  }
}
