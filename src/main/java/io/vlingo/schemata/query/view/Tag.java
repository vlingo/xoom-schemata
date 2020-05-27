// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

public class Tag {
    public final String id;
    public final String name;

    public static Tag of(final String id, final String name) {
        return new Tag(id, name);
    }

    public static Tag only(final String id) {
        return new Tag(id, "");
    }

    public Tag(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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

        return id.equals(((Tag) other).id);
    }

    @Override
    public String toString() {
        return "Tag [id=" + id + ", name=" + name + "]";
    }
}
