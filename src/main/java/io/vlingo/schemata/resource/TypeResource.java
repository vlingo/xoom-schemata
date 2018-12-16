package io.vlingo.schemata.resource;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.schemata.codegen.backends.java.JavaCodeGenerator;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.resource.ResourceBuilder.*;

public final class TypeResource {
    public Resource resources(int threadPool) {
        return resource(TypeResource.class.getSimpleName(), threadPool,
                post("/{organization}/{unit}/{context}/{schemaVersion}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(this::pushType),
                put("/{organization}/{unit}/{context}/{schemaVersion}/{typeName}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::publishType),
                get("/{organization}/{unit}/{context}/{schemaVersion}/{typeName}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::pullType),
                delete("/{organization}/{unit}/{context}/{schemaVersion}/{typeName}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .handle(this::deleteType));
    }

    private String inMemoryType;

    private Completes<Response> pushType(String organization, String unit, String context, String schemaVersion, String body) {
        inMemoryType = body;

        return Completes.withSuccess(
                Response.of(Response.Status.Created, serialized(body))
        );
    }

    private Completes<Response> publishType(String organization, String unit, String context, String schemaVersion, String typeName) {
        return Completes.withSuccess(
                Response.of(Response.Status.Ok, serialized("x"))
        );
    }

    private Completes<Response> pullType(String organization, String unit, String context, String schemaVersion, String typeName) {
        String javaClass = TypeDefinitionCompiler.backedBy(new JavaCodeGenerator()).compile(inMemoryType);
        return Completes.withSuccess(
                Response.of(Response.Status.Ok, javaClass)
        );
    }

    private Completes<Response> deleteType(String organization, String unit, String context, String schemaVersion, String typeName) {
        return Completes.withSuccess(
                Response.of(Response.Status.Ok, serialized("x"))
        );
    }
}
