package io.vlingo.schemata.errors;


import java.util.*;

public class SchemataBusinessException extends RuntimeException {

  public enum Code {
    NOT_FOUND,
    INVALID_REFERENCE,
    INVALID_SCHEMA_DEFINITION,
    CODE_GENERATION_ERROR,
    NOT_AUTHORIZED
  }

  protected final Map<String, Object> context = new HashMap<>();
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

  public static SchemataBusinessException invalidSchemaDefinition(String message, List<? extends Throwable> causes) {
    SchemataBusinessException ex = new SchemataBusinessException(Code.INVALID_SCHEMA_DEFINITION, message);
    ex.context.put("causes", causes);
    return ex;
  }

  public static SchemataBusinessException invalidSchemaDefinition(String message, Throwable cause) {
    return invalidSchemaDefinition(message, Collections.singletonList(cause));
  }

  public static SchemataBusinessException invalidSchemaDefinition() {
    return new SchemataBusinessException(Code.INVALID_SCHEMA_DEFINITION, "Schema definition invalid");
  }

  public static SchemataBusinessException codeGenerationError(Exception e) {
    SchemataBusinessException ex = new SchemataBusinessException(Code.CODE_GENERATION_ERROR, "Code generation error");
    ex.context.put("codeGenerationException",e);
    return ex;
  }
  public static SchemataBusinessException notAuthorized(String subject) {
    SchemataBusinessException ex = new SchemataBusinessException(Code.NOT_AUTHORIZED, "Not authorized to access subject");
    ex.context.put("subject",subject);
    return ex;
  }
}
