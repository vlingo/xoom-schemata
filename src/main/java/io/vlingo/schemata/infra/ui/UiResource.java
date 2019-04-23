package io.vlingo.schemata.infra.ui;

import io.vlingo.common.Completes;
import io.vlingo.http.Body;
import io.vlingo.http.Header;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
      get("/app/")
        .handle(impl::serve),
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
    if (pathSegments.length == 0)
      return serve("index.html");

    String path = pathFrom(pathSegments);
    try {
      byte[] content = readFileFromClasspath(path);
      return Completes.withSuccess(
        Response.of(Ok,
          Header.Headers.of(
            ResponseHeader.of(ContentType, guessContentType(path)),
            ResponseHeader.of(ContentLength, content.length)
          ),
          Body.from(content, Body.Encoding.UTF8).content //FIXME: This will not work for binary files; rather find out how to send a plain byte[]
        ));
    } catch (FileNotFoundException e) {
      return Completes.withSuccess(Response.of(NotFound, path + " not found."));
    } catch (IOException e) {
      return Completes.withSuccess(Response.of(InternalServerError));
    }
  }

  private String guessContentType(String path) throws IOException {
    String contentType = Files.probeContentType(Paths.get(path));
    contentType = (contentType != null) ? contentType : "application/octet-stream";
    if (contentType.equals("application/octet-stream")) {
      if (path.endsWith(".js")) contentType = "text/javascript";
      else if (path.endsWith(".css")) contentType = "text/css";
    }
    return contentType;
  }

  private String pathFrom(String[] pathSegments) {
    return Stream.of(pathSegments)
      .map(p -> p.startsWith("/") ? p.substring(1) : p)
      .map(p -> p.endsWith("/") ? p.substring(0, p.length() - 1) : p)
      .collect(Collectors.joining("/", "frontend/", ""));
  }

  private byte[] readFileFromClasspath(final String path) throws IOException {
    InputStream is = getClass().getClassLoader().getResourceAsStream(path);

    if (is == null)
      throw new FileNotFoundException();

    return read(is);
  }

  private static byte[] read(InputStream is) throws IOException {
    byte[] readBytes;
    byte[] buffer = new byte[4096];

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      int read;
      while ((read = is.read(buffer)) != -1) {
        baos.write(buffer, 0, read);
      }
      readBytes = baos.toByteArray();
    } finally {
      is.close();
    }
    return readBytes;
  }


}
