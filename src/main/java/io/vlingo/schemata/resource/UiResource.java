package io.vlingo.schemata.resource;

import io.vlingo.common.Completes;
import io.vlingo.http.Body;
import io.vlingo.http.Header;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.ContentLength;
import static io.vlingo.http.ResponseHeader.ContentType;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

/**
 * Serves the files making up the UI from the classpath.
 * Assumes the generated UI resources to be present in
 * {@code static/} with in the jar or {@code resources/}.
 *
 * <em>WARNING:</em> Note that the current implementation is only able to handle resource
 * directory trees up to three levels deep.
 * <p>
 * FIXME: This leaves the server wide open for read access. We should constrain access to the resources we actually provide.
 */
public class UiResource extends ResourceHandler {
  public static Resource asResource() {
    UiResource impl = new UiResource();

    return resource("ui", 10,
      get("/app/{file}")
        .param(String.class)
        .handle(impl::serve),
      get("/app/{path1}/{file}")
        .param(String.class)
        .param(String.class)
        .handle(impl::serve),
      get("/app/{path1}/{path2}/{file}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(impl::serve),
      get("/app/{path1}/{path2}/{path3}/{file}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(impl::serve)
    );
  }

  private Completes<Response> serve(final String... pathSegments) {
    String path = pathFrom(pathSegments);
    try {
      String contentType = Files.probeContentType(Paths.get(path));
      contentType = (contentType != null) ? contentType : "application/octet-stream";

      byte[] content = readFileFromClasspath(path);

      return Completes.withSuccess(
        Response.of(Ok,
          Header.Headers.of(
            ResponseHeader.of(ContentType, contentType),
            ResponseHeader.of(ContentLength, content.length)
          ),
          Body.from(content).content
        ));
    } catch (URISyntaxException e) {
      return Completes.withSuccess(Response.of(BadRequest));
    } catch (FileNotFoundException e) {
      return Completes.withSuccess(Response.of(NotFound, path));
    } catch (IOException e) {
      return Completes.withSuccess(Response.of(InternalServerError));
    }
  }

  private String pathFrom(String[] pathSegments) {
    return Stream.of(pathSegments)
      .map(p -> p.startsWith("/") ? p.substring(1) : p)
      .map(p -> p.endsWith("/") ? p.substring(0, p.length() - 1) : p)
      .collect(Collectors.joining("/"));
  }

  private byte[] readFileFromClasspath(final String path) throws URISyntaxException, FileNotFoundException, IOException {
    URL resource = getClass().getClassLoader().getResource(path);

    if (resource == null)
      throw new FileNotFoundException();

    return Files.readAllBytes(Paths.get(resource.toURI()));
  }


}
