// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.lattice.model.identity.IdentityGeneratorType;

import java.util.UUID;

public abstract class Id {
    public final String value;

    public final boolean isDefined() {
        return value.length () > 0;
    }

    public final boolean isUndefined() {
        return value.isEmpty ();
    }

    public Id(final String value) {
        this.value = value;
    }

    public static class OrganizationId extends Id {

        public OrganizationId(final String value) {
            super ( value );
        }

        public static OrganizationId existing(final String id) {
            return new OrganizationId ( UUID.fromString ( id ).toString () );
        }

        public static OrganizationId undefined() {
            return new OrganizationId ( "" );
        }

        public static OrganizationId unique() {
            return new OrganizationId ( IdentityGeneratorType.Random.generate ().toString () );
        }
    }

    public static class UnitId extends Id {

        public UnitId(final String value) {
            super ( value );
        }

        public static UnitId existing(final String id) {
            return new UnitId ( UUID.fromString ( id ).toString () );
        }

        public static UnitId undefined() {
            return new UnitId ( "" );
        }

        public static UnitId unique() {
            return new UnitId ( IdentityGeneratorType.Random.generate ().toString () );
        }
    }

    public static class ContextId extends Id {

        public ContextId(final String value) {
            super ( value );
        }

        public static ContextId existing(final String id) {
            String var10002 = UUID.fromString ( id ).toString ();
            return new ContextId ( var10002 );
        }

        public static ContextId undefined() {
            return new ContextId ( "" );
        }

        public static ContextId unique() {
            String var10002 = IdentityGeneratorType.Random.generate ().toString ();
            return new ContextId ( var10002 );
        }
    }

    public static class SchemaId extends Id {

        public SchemaId(final String value) {
            super ( value );
        }

        public static SchemaId existing(final String id) {
            return new SchemaId ( UUID.fromString ( id ).toString () );
        }

        public static SchemaId undefined() {
            return new SchemaId ( "" );
        }

        public static SchemaId unique() {
            return new SchemaId ( UUID.randomUUID ().toString () );
        }
    }

    public static class SchemaVersionId extends Id {

        public SchemaVersionId(final String value) {
            super ( value );
        }

        public static SchemaVersionId existing(final String id) {
            return new SchemaVersionId ( UUID.fromString ( id ).toString () );
        }

        public static SchemaVersionId undefined() {
            return new SchemaVersionId ( "" );
        }

        public static SchemaVersionId unique() {
            return new SchemaVersionId ( IdentityGeneratorType.Random.generate ().toString () );
        }
    }
}
