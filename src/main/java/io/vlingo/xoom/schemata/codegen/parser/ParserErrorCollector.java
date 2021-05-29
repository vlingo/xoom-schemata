// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.codegen.parser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ParserErrorCollector implements ANTLRErrorListener {
  private List<String> errors = new ArrayList<>();

  public boolean hasErrors() {
    return errors.size() != 0;
  }

  public String errorsStartingWith(final String header) {
    final StringBuilder builder = new StringBuilder();

    builder.append(header).append("\n");

    errors.forEach(error -> builder.append(error).append("\n"));

    return builder.toString();
  }

  @Override
  public String toString() {
    return "ParserErrorStrategy [errors=" + errors + "]";
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
    errors.add(gather(offendingSymbol, line, charPositionInLine, msg, e));
  }

  @Override
  public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {

  }

  @Override
  public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {

  }

  @Override
  public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {

  }

  private String gather(Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException exception) {
    final StringBuilder builder = new StringBuilder();

    builder.append("Error [").append(line).append(",").append(charPositionInLine).append("]: ").append(msg);

    if (exception != null) {
      builder.append(exception.getMessage());

      for (final StackTraceElement stackTraceThrowable : exception.getStackTrace()) {
        builder.append("\n").append(stackTraceThrowable);
      }
    }

    return builder.toString();
  }
}
