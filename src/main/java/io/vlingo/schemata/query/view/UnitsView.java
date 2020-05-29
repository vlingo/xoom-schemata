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
    private final List<Tag> units;

    public static UnitsView empty() {
        return new UnitsView();
    }

    private UnitsView() {
        this.units = new ArrayList<>();
    }

    private UnitsView(List<Tag> units) {
        this.units = units;
    }

    public UnitsView add(final Tag unit) {
        if (units.contains(unit)) {
            return this;
        } else {
            UnitsView result = new UnitsView(new ArrayList<>(units));
            result.units.add(unit);

            return result;
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

    public UnitsView replace(final Tag unit) {
        final int index = units.indexOf(unit);
        if (index >= 0) {
            UnitsView result = new UnitsView(new ArrayList<>(units));
            result.units.set(index, unit);

            return result;
        } else {
            return this;
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
