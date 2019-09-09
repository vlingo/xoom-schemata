// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

public class Naming {
  public static final String InvalidNameMessage = "Name must not be null, empty, or have ':' or '/' characters; invalid: ";

  public static void assertValid(final String name) {
    if (!isValid(name)) {
      throw new IllegalArgumentException(invalidNameMessage(name));
    }
  }

  public static String invalidNameMessage(final String name) {
    return InvalidNameMessage + name;
  }

  public static boolean isValid(final String name) {
    if (name == null || name.isEmpty() || name.contains(":") || name.contains("/")) {
      return false;
    }
    return true;
  }
}
