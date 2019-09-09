// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.common.Tuple3;

public class AuthorizationData {
  public static final String AuthorizationType = "VLINGO-SCHEMATA";
  public static final String DependentKey = "dependent";
  public static final String SourceKey = "source";

  private static final int TypeIndex = 0;
  private static final int SourceIndex = 1;
  private static final int DependentIndex = 2;

  public final String dependent;
  public final String source;
  public final String type;

  public static AuthorizationData with(final String value) {
    return new AuthorizationData(value);
  }

  private AuthorizationData(final String value) {
    final Tuple3<String,String,String> data = parse(value);
    this.type = data._1;
    this.source = data._2;
    this.dependent = data._3;
  }

  private Tuple3<String, String, String> parse(final String value) {
    int resultIndex = 0;
    final String[] result = new String[3];
    final String[] parts = value.split(" ");

    for (final String part : parts) {
      final String clean = part.trim();
      if (!clean.isEmpty()) {
        result[resultIndex++] = clean;
      }
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
