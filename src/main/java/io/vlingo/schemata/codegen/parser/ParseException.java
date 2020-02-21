package io.vlingo.schemata.codegen.parser;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class ParseException extends RuntimeException {
  private List<Throwable> exceptions = new ArrayList<>();

  public ParseException() {
    super();
  }

  public ParseException(String message) {
    super(message);
  }

  public ParseException(String message, Throwable cause) {
    super(message, cause);
    exceptions.add(cause);
  }

  public ParseException(String message, List<? extends Throwable> causes) {
    super(message);
    exceptions.addAll(causes);
  }

  public ParseException(List<Throwable> causes) {
    super();
    exceptions.addAll(causes);
  }

  public ParseException(Throwable cause) {
    super(cause);
    exceptions.add(cause);
  }

  @Override
  public String getMessage() {
    String containedMessages = exceptions.stream()
            .map(e -> e.getMessage() == null ? e.getClass().getName() : e.getClass().getName() + ": " + e.getMessage())
            .collect(joining(", ","[","]"));
    return super.getMessage() + (containedMessages.length() != 0 ? ": " + containedMessages : "");
  }

  public List<Throwable> getExceptions() {
    return exceptions;
  }
}
