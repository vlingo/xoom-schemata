// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;

public interface SchemaVersion {
    static Id.SchemaVersionId uniqueId() {
        return Id.SchemaVersionId.unique();
    }

    static SchemaVersion newWith(final Stage stage, final String name, final String description, final Definition definition, final Status status, final Version version){
        return stage.actorFor(io.vlingo.actors.Definition.has(SchemaVersionEntity.class, io.vlingo.actors.Definition.parameters(Organization.uniqueId(), Unit.uniqueId(),
                Context.uniqueId(), Schema.uniqueId(), uniqueId(), name, description, definition, status, version)), SchemaVersion.class);
    }

    void describeAs(final String description);

    void definedAs(final Definition definition);

    void assignStatus(final Status status);

    void assignVersion(final Version version);

    class Definition {
        public final String value;

        public Definition(final String value) {
            this.value = value;
        }
    }

    enum Status {
        Draft, Published, Removed, Undefined;
    }

    class Version {
        public final String value;

        public Version(final String value) {
            this.value = value;
        }
    }
}
