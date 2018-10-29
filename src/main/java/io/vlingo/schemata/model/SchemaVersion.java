// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

public interface SchemaVersion {
  void describeAs(final String description);
  void definedAs(final String defintion);
  void assignStatus(final Status status);

  public static class Definition {
    public final String value;

    public Definition(final String value) {
      this.value = value;
    }
  }

  public static enum Status {
    Draft, Published, Removed, Undefined;
  }

  public static class Version {
    public final String value;

    public Version(final String value) {
      this.value = value;
    }
  }
}
