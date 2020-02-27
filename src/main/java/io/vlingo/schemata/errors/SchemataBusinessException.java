package io.vlingo.schemata.errors;

import java.util.HashMap;
import java.util.Map;

public class SchemataBusinessException extends RuntimeException {

  public enum Code {NOT_FOUND, INVALID_REFERENCE}

  protected final Map<String, String> context = new HashMap<>();
  public final Code errorCode;

  private SchemataBusinessException(Code errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  private SchemataBusinessException(Code errorCode, String message, Map<String, String> context) {
    this(errorCode, message);
    this.context.putAll(context);
  }

  public static SchemataBusinessException notFound(String type, Map<String, String> queryParameters) {
    return new SchemataBusinessException(Code.NOT_FOUND, type + " not found", queryParameters);
  }

  public static SchemataBusinessException invalidReference(String reference, Map<String, String> context) {
    return new SchemataBusinessException(Code.INVALID_REFERENCE, reference + " invalid", context);
  }

  public static SchemataBusinessException invalidReference(String reference) {
    return new SchemataBusinessException(Code.INVALID_REFERENCE, reference + " invalid");
  }
}
