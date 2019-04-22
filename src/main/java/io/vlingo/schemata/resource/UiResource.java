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
 * NOTES: UTF-8; nested only 3 levels deep
 * TODO: guess content header & send back byte[]
 */
public class UiResource extends ResourceHandler {
  public static Resource asResource() {
    UiResource impl = new UiResource();

    // CAVEAT: We handle only resources nested up until three levels deep.
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
      byte[] content = readFileFromClasspath(path);

      return Completes.withSuccess(
        Response.of(Response.Status.Ok,
          Header.Headers.of(
            ResponseHeader.of(ContentType, contentType),
            ResponseHeader.of(ContentLength, content.length)
          ),
          Body.from(content).content
        ));
    } catch (URISyntaxException e) {
      return Completes.withFailure(Response.of(BadRequest, e.getMessage()));
    } catch (FileNotFoundException e) {
      return Completes.withFailure(Response.of(NotFound, path));
    } catch (IOException e) {
      return Completes.withFailure(Response.of(InternalServerError, e.getMessage()));
    }
  }

  private String pathFrom(String[] pathSegments) {
    return Stream.of(pathSegments)
      .map(p -> p.startsWith("/") ? p.substring(1) : p)
      .map(p -> p.endsWith("/") ? p.substring(0, p.length() - 1) : p)
      .collect(Collectors.joining("/"));
  }

  public byte[] readFileFromClasspath(final String path) throws URISyntaxException, FileNotFoundException, IOException {
    URL resource = getClass().getClassLoader().getResource(path);

    if (resource == null)
      throw new FileNotFoundException(path + " not found in classpath.");

    return Files.readAllBytes(Paths.get(resource.toURI()));
  }


}
