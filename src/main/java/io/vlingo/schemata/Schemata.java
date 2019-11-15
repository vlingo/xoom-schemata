// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

public class Schemata {
  //==========================
  // IDs
  //==========================

  public static final String NoId = "(id?)";

  public static final String ReferenceSeparator = ":";

  //==========================
  // Paths
  //==========================

  public static final String OrganizationsPath  = "/organizations/%s";

  public static final String UnitsPath          = "/organizations/%s/units/%s";

  public static final String ContextsPath       = "/organizations/%s/units/%s/contexts/%s";

  public static final String SchemasPath        = "/organizations/%s/units/%s/contexts/%s/schemas/%s";

  public static final String SchemaVersionsPath = "/organizations/%s/units/%s/contexts/%s/schemas/%s/versions/%s";

  //==========================
  // Names
  //==========================

  public static final String StageName = "vlingo-schemata-grid";
}
