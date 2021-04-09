// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

public class UnitView {
    private final String unitId;
    private final String name;
    private final String description;

    public static UnitView empty() {
        return new UnitView();
    }

    public static UnitView with(final String unitId) {
        return new UnitView(unitId);
    }

    public static UnitView with(final String unitId, final String name, final String description) {
        return new UnitView(unitId, name, description);
    }

    private UnitView(final String unitId, final String name, final String description) {
        this.unitId = unitId;
        this.name = name;
        this.description = description;
    }

    private UnitView(final String unitId) {
        this(unitId, "", "");
    }

    private UnitView() {
        this("", "", "");
    }

    public String unitId() {
        return unitId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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

        return unitId.equals(((UnitView) other).unitId);
    }

    public UnitView mergeNameWith(String unitId, String name) {
        if (this.unitId.equals(unitId)) {
            return new UnitView(this.unitId, name, this.description);
        } else {
            return this;
        }
    }

    public UnitView mergeDescriptionWith(String unitId, String description) {
        if (this.unitId.equals(unitId)) {
            return new UnitView(this.unitId, this.name, this.description);
        } else {
            return this;
        }
    }

    public UnitView mergeWith(String unitId, String name, String description) {
        if (this.unitId.equals(unitId)) {
            return new UnitView(this.unitId, name, description);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "UnitView [unitId=" + unitId + ", name=" + name + ", description=" + description + "]";
    }
}
