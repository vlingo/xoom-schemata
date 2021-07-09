// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.schemata.Schemata;

public class Path {
    public final String organization;
    public final String unit;
    public final String context;
    public final String schema;
    public final String version;

    public boolean hasOrganization() {
        return !organization.isEmpty();
    }

    public boolean hasUnit() {
        return !unit.isEmpty();
    }

    public boolean hasContext() {
        return !context.isEmpty();
    }

    public boolean hasSchema() {
        return !schema.isEmpty();
    }

    public boolean hasVersion() {
        return !version.isEmpty();
    }

    public String versionOrElse(final String whenNoVersion) {
        return hasVersion() ? version : whenNoVersion;
    }

    public String toReference() {
        final String[] parts = hasVersion()
                ? new String[] { organization, unit, context, schema, version }
                : new String[] { organization, unit, context, schema };
        return String.join(Schemata.ReferenceSeparator, parts);
    }

    @Override
    public String toString() {
        return "Path [organization=" + organization + ", unit=" + unit + ", context=" + context + ", schema=" + schema
                + ", version=" + version + "]";
    }

    public static Path with(final String organization, final String unit, final String context, final String schema) {
        return new Path(organization, unit, context, schema);
    }

    public static Path with(final String organization, final String unit, final String context, final String schema, final String version) {
        return new Path(organization, unit, context, schema, version);
    }

    public static Path with(final String reference, final boolean versionOptional) {
        final String[] parts = reference.split(Schemata.ReferenceSeparator);

        if (!versionOptional && parts.length != Schemata.MaxReferenceParts) {
            throw new IllegalArgumentException("The reference path must have five parts: org:unit:context:schema:version");
        }

        if (parts.length < Schemata.MinReferenceParts) {
            throw new IllegalArgumentException("The reference path must have five parts: org:unit:context:schema[:version]");
        }

        return new Path(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts.length == 5 ? parts[4].trim() : "");
    }

    public static boolean isValidReference(final String reference, final boolean versionOptional) {
        final String[] parts = reference.split(Schemata.ReferenceSeparator);

        return parts.length == Schemata.MaxReferenceParts || (versionOptional && parts.length == Schemata.MinReferenceParts);
    }

    private Path(final String organization, final String unit, final String context, final String schema, final String version) {
        this.organization = organization;
        this.unit = unit;
        this.context = context;
        this.schema = schema;
        this.version = version;
    }

    private Path(final String organization, final String unit, final String context, final String schema) {
        this(organization, unit, context, schema, "");
    }

    private Path(final String organization, final String unit, final String context) {
        this(organization, unit, context, "", "");
    }

    private Path(final String organization, final String unit) {
        this(organization, unit, "", "", "");
    }

    private Path(final String organization) {
        this(organization, "", "", "", "");
    }

    public Path withSchema(final String schema) {
        return Path.with(organization, unit, context, schema, version);
    }
}
