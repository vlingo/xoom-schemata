package io.vlingo.schemata.errors;

import java.util.Map;

public class EntityNotFoundException extends RuntimeException {
  private final Map<String, String> context;

  public EntityNotFoundException() {
    super();
    context = null;
  }

  public EntityNotFoundException(String type, Map<String, String> queryParameters) {
    super(type + " not found");
    context = queryParameters;
  }
}
