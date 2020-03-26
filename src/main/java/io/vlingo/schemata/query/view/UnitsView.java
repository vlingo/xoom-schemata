// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitsView {
  private List<Tag> units;

  public static UnitsView empty() {
    return new UnitsView();
  }

  public UnitsView() {
    this.units = new ArrayList<>();
  }

  public void add(final Tag unit) {
    if (!units.contains(unit)) {
      units.add(unit);
    }
  }

  public Tag get(final String unitId) {
    Tag unit = Tag.only(unitId);

    final int index = units.indexOf(unit);

    if (index >= 0) {
      unit = units.get(index);
    }

    return unit;
  }

  public void replace(final Tag unit) {
    final int index = units.indexOf(unit);
    if (index >= 0) {
      units.set(index, unit);
    }
  }

  public List<Tag> all() {
    return Collections.unmodifiableList(units);
  }

  @Override
  public String toString() {
    return "UnitsView [units=" + units + "]";
  }
}
