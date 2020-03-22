// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import java.util.Arrays;
import java.util.List;

import io.vlingo.common.Tuple3;
import io.vlingo.schemata.Schemata;

public class AuthorizationData {
  public static final String AuthorizationType = "VLINGO-SCHEMATA";
  public static final String DependentKey = "dependent";
  public static final String SourceKey = "source";

  private static List<String> NoSpaceFollows = Arrays.asList(DependentKey, SourceKey, "=");

  private static final int TypeIndex = 0;
  private static final int SourceIndex = 1;
  private static final int DependentIndex = 2;

  public final String dependent;
  public final String source;
  public final String type;

  public static AuthorizationData with(final String value) {
    return new AuthorizationData(value);
  }

  public Tuple3<String,String,String> dependentAsIds() {
    final String[] ids = dependent.split(Schemata.ReferenceSeparator);

    return Tuple3.tuple(ids[0], ids[1], ids[2]);
  }

  public String sourceAsId() {
    return source;
  }

  public boolean sameDependent(final String... others) {
    return dependent.equals(format(others));
  }

  public boolean sameSource(final String... others) {
    return source.equals(format(others));
  }

  @Override
  public String toString() {
    return "AuthorizationData [type=" + type + ", source=" + source + ", dependent=" + dependent + "]";
  }

  private AuthorizationData(final String value) {
    final Tuple3<String,String,String> data = parse(value);
    this.type = data._1;
    this.source = data._2;
    this.dependent = data._3;
  }

  private String format(final String[] others) {
    final StringBuilder builder = new StringBuilder();
    String separator = "";

    for (int idx = 0; idx < others.length; ++idx) {
      builder.append(separator).append(others[idx]);
      separator = Schemata.ReferenceSeparator;
    }

    return builder.toString();
  }

  private Tuple3<String, String, String> parse(final String value) {

    final StringBuilder builder = new StringBuilder();
    for (final String part : value.split(" ")) {
      final String clean = part.trim();
      if (!clean.isEmpty()) {
        builder.append(clean);
        if (!NoSpaceFollows.contains(clean)) {
          builder.append(" ");
        }
      }
    }

    final String[] result = builder.toString().trim().split(" ");

    if (result.length != 3) {
      throw new IllegalArgumentException("Wrong number of parameters in: " + value);
    }

    return Tuple3.from(part(result, TypeIndex), part(result, SourceIndex), part(result, DependentIndex));
  }

  private String part(final String[] result, final int index) {
    if (result[index] != null && !result[index].isEmpty()) {
      switch (index) {
      case TypeIndex:
        assert AuthorizationType.equals(result[index]) : "Wrong authorization type: " + result[index];
        return result[index];
      case SourceIndex:
        return valueOf(result[index], SourceKey);
      case DependentIndex:
        return valueOf(result[index], DependentKey);
      default:
        return "";
      }
    }

    throw new IllegalArgumentException("Unknown part: " + result[index]);
  }

  private String valueOf(final String attribute, final String key) {
    final String[] kv = attribute.split("=");
    final String k = kv[0].trim();
    assert k.equals(key) : "Unexpected attribute: " + attribute;
    return kv[1].trim();
  }
}
