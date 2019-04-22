package io.vlingo.schemata.resource;

import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.put;
import static io.vlingo.http.resource.ResourceBuilder.resource;

import io.vlingo.common.Completes;
import io.vlingo.http.Header;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.schemata.codegen.TypeDefinitionCompiler;
import io.vlingo.schemata.codegen.backends.java.JavaCodeGenerator;

public class SchemaResource {
    @SuppressWarnings("rawtypes")
    public static Resource asResource() {
        SchemaResource impl = new SchemaResource();

        return resource("schema", 10,
                put("/api/{organization}/{context}/{unit}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .body(String.class)
                        .handle(impl::createSchema),
                get("/api/{organization}/{context}/{unit}/{qualifiedName}")
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .param(String.class)
                        .header("Accept")
                        .handle(impl::getSchema));
    }

    private String schemaDef;

    private Completes<Response> createSchema(
            String organization,
            String context,
            String unit,
            String schemaDefinition
    ) {
        this.schemaDef = new String(schemaDefinition);

        return Completes.withSuccess(Response.of(Response.Status.Created,
                Header.Headers.of(
                        ResponseHeader.of("Location",
                                String.format("/%s/%s/%s/%s:%s", organization, context, unit, "name", "0.0.0")))));
    }

    private Completes<Response> getSchema(
            String organization,
            String context,
            String unit,
            String qualifiedName,
            Header language
    ) {
        if (!language.value.equals("x-vlingo-schema/java")) {
            return Completes.withSuccess(Response.of(Response.Status.BadRequest));
        }

        String code = TypeDefinitionCompiler.backedBy(new JavaCodeGenerator()).compile(schemaDef);
        return Completes.withSuccess(Response.of(Response.Status.Ok, code));
    }
}
