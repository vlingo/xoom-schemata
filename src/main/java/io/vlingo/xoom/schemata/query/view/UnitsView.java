// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitsView {
    private final List<UnitItem> units;

    public static UnitsView empty() {
        return new UnitsView();
    }

    private UnitsView() {
        this.units = new ArrayList<>();
    }

    private UnitsView(List<UnitItem> units) {
        this.units = units;
    }

    public UnitsView add(final UnitItem unit) {
        if (units.contains(unit)) {
            return this;
        } else {
            UnitsView result = new UnitsView(new ArrayList<>(units));
            result.units.add(unit);

            return result;
        }
    }

    public UnitItem get(final String unitId) {
        UnitItem unit = UnitItem.only(unitId);

        final int index = units.indexOf(unit);

        if (index >= 0) {
            unit = units.get(index);
        }

        return unit;
    }

    public UnitsView replace(final UnitItem unit) {
        final int index = units.indexOf(unit);
        if (index >= 0) {
            UnitsView result = new UnitsView(new ArrayList<>(units));
            result.units.set(index, unit);

            return result;
        } else {
            return this;
        }
    }

    public List<UnitItem> all() {
        return Collections.unmodifiableList(units);
    }

    @Override
    public String toString() {
        return "UnitsView [units=" + units + "]";
    }

    public static class UnitItem {
        public final String unitId;
        public final String name;
        public final String organizationId;
        public final String description;

        public static UnitItem of(final String unitId, final String name, final String organizationId, final String description) {
            return new UnitItem(unitId, name, organizationId, description);
        }
// TODO: see if this works -> if I made a unit and renamed it, is it still giving back unitId, name, description and orgId?
        public static UnitItem of(final String unitId, final String name) {
            return new UnitItem(unitId, name, "", "");
        }

        public static UnitItem only(final String unitId) {
            return new UnitItem(unitId, "", "", "");
        }

        public UnitItem(final String unitId, final String name, final String organizationId, final String description) {
            this.unitId = unitId;
            this.name = name;
            this.organizationId = organizationId;
            this.description = description;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            return unitId.equals(((UnitItem) other).unitId);
        }

        @Override
        public String toString() {
            return "UnitItem [unitId=" + unitId + ", name=" + name + "]";
        }
    }
}
