package io.vlingo.schemata.model;

import io.vlingo.lattice.model.identity.IdentityGeneratorType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Chandrabhan Kumhar
 * Used to set unique id for every schemata
 */
public abstract class Id {
    @NotNull
    private final String value;

    @NotNull
    public final String getValue() {
        return this.value;
    }

    public final boolean isDefined() {
        CharSequence var1 = this.value;
        return var1.length () > 0;
    }

    public final boolean isUndefined() {
        CharSequence var1 = this.value;
        return var1.length () == 0;
    }

    public Id(@NotNull String value) {
        super ();
        this.value = value;
    }

    /**
     * Inner class for organization id to set unique or different id
     */
    public static class OrganizationId extends Id {

        public OrganizationId(@NotNull String value) {
            super ( value );
        }

        public static class Companion {
            @NotNull
            public static Id.OrganizationId existing(@NotNull String id) {
                String var10002 = UUID.fromString ( id ).toString ();
                return new Id.OrganizationId ( var10002 );
            }

            @NotNull
            public static Id.OrganizationId undefined() {
                return new Id.OrganizationId ( "" );
            }

            @NotNull
            public static Id.OrganizationId unique() {
                String var10002 = IdentityGeneratorType.Random.generate ().toString ();
                return new Id.OrganizationId ( var10002 );
            }


        }
    }

    /**
     * Inner class to set unique or different context id
     */
    public static class ContextId extends Id {

        public ContextId(@NotNull String value) {
            super ( value );
        }

        public static class Companion {
            @NotNull
            public static Id.ContextId existing(@NotNull String id) {
                String var10002 = UUID.fromString ( id ).toString ();
                return new Id.ContextId ( var10002 );
            }

            @NotNull
            public static Id.ContextId undefined() {
                return new Id.ContextId ( "" );
            }

            @NotNull
            public static Id.ContextId unique() {
                String var10002 = IdentityGeneratorType.Random.generate ().toString ();
                return new Id.ContextId ( var10002 );
            }

        }
    }

    /**
     * Inner class to set unique or different schema id
     */
    public static class SchemaId extends Id {

        public SchemaId(String value) {
            super ( value );
        }


        public static class Companion {
            @NotNull
            public static Id.SchemaId existing(@NotNull String id) {
                String var10002 = UUID.fromString ( id ).toString ();
                return new Id.SchemaId ( var10002 );
            }

            @NotNull
            public static Id.SchemaId undefined() {
                return new Id.SchemaId ( "" );
            }

            @NotNull
            public static Id.SchemaId unique() {
                String var10002 = IdentityGeneratorType.Random.generate ().toString ();
                return new Id.SchemaId ( var10002 );
            }

        }
    }

    /**
     * Inner class to set unique or different schema version id
     */
    public static class SchemaVersionId extends Id {

        public SchemaVersionId(String value) {
            super ( value );
        }


        public static class Companion {
            @NotNull
            public static Id.SchemaVersionId existing(@NotNull String id) {
                String var10002 = UUID.fromString ( id ).toString ();
                return new Id.SchemaVersionId ( var10002 );
            }

            @NotNull
            public static Id.SchemaVersionId undefined() {
                return new Id.SchemaVersionId ( "" );
            }

            @NotNull
            public static Id.SchemaVersionId unique() {
                String var10002 = IdentityGeneratorType.Random.generate ().toString ();
                return new Id.SchemaVersionId ( var10002 );
            }

        }
    }
}

