package io.vlingo.schemata.resource;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;

public final class TypeResource {
    public Resource resources(int threadPool) {
        return resource(TypeResource.class.getSimpleName(), threadPool,
                post("/{organization}/{unit}/{context}/{schemaVersion}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::pushType));
    }

    private Completes<Response> pushType(String organization, String unit, String context, String schemaVersion, String body) {
        return Completes.withSuccess(
                Response.of(Response.Status.Created, serialized(body))
        );
    }
}
