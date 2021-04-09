// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.schemata.Schemata;

import java.util.Arrays;

public class FullyQualifiedReference {

    public final String organization;
    public final String unit;
    public final String context;
    public final String schema;
    public final String schemaVersion;

    public static FullyQualifiedReference identity(final String organization, final String unit, final String context, final String schema, final String schemaVersion) {
        return new FullyQualifiedReference(organization, unit, context, schema, schemaVersion);
    }

    public static FullyQualifiedReference from(String reference) {
        String[] parts = reference.split(Schemata.ReferenceSeparator);

        ensureValidReference(parts);

        if (parts.length > Schemata.MinReferenceParts) {
            return new FullyQualifiedReference(parts[0], parts[1], parts[2], parts[3], parts[4]);
        } else {
            return new FullyQualifiedReference(parts[0], parts[1], parts[2], parts[3]);
        }

    }

    public boolean isSchemaVersionReference() {
        return this.schemaVersion != null;
    }

    private static void ensureValidReference(String[] parts) {

        if (!Arrays.stream(parts).allMatch(p -> p.length() > 0)) {
            throw new IllegalArgumentException(
                    "Invalid reference. Organization, Unit, Context and Schema parts must be set to non empty values.");
        }

        if (parts.length < Schemata.MinReferenceParts || parts.length > Schemata.MaxReferenceParts) {
            throw new IllegalArgumentException(
                    "Invalid reference. Valid references look like <org>:<unit>:<context>:<schema>[:<version>]");
        }
    }

    private FullyQualifiedReference(final String organization, final String unit, final String context, final String schema) {
        this.organization = organization;
        this.unit = unit;
        this.context = context;
        this.schema = schema;
        this.schemaVersion = null;
    }

    private FullyQualifiedReference(final String organization, final String unit, final String context, final String schema, final String schemaVersion) {
        this.organization = organization;
        this.unit = unit;
        this.context = context;
        this.schema = schema;
        this.schemaVersion = schemaVersion;
    }

    @Override
    public String toString() {
        String ref = String.join(Schemata.ReferenceSeparator, organization, unit, context, schema);
        return isSchemaVersionReference()
                ? String.join(Schemata.ReferenceSeparator, ref, schemaVersion)
                : ref;
    }
}
