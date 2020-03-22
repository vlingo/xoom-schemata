package io.vlingo.schemata.codegen.parser;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;

import java.util.ArrayList;
import java.util.List;

public class ParserErrorStrategy extends DefaultErrorStrategy {
  private List<Exception> recognitionExceptions = new ArrayList<>();
  @Override
  public void reportError(Parser recognizer, RecognitionException e) {
    super.reportError(recognizer, e);
    recognitionExceptions.add(e);
  }

  public boolean hasErrors() {
    return recognitionExceptions.size() != 0;
  }

  public List<Exception> errors() {
    return recognitionExceptions;
  }
}
