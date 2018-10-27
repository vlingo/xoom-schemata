package io.vlingo.schemata.model.schemaversion;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chandrabhan Kumhar
 * Used to perform schema version operations
 */
public final class SchemaVersion {

    private SchemaVersion.State state = new SchemaVersion.State ( (OrganizationId) null, (ContextId) null, (SchemaId) null, (SchemaVersionId) null, (String) null, (SchemaVersion.Definition) null, (SchemaVersion.Version) null, Status.Undefined );

    /**
     * Used to get state of schema version
     *
     * @return
     */
    public final SchemaVersion.State getState() {
        return this.state;
    }


    /**
     * Inner class of schema state
     */
    public class State {
        @NotNull
        private ContextId contextId;
        @NotNull
        private SchemaVersion.Definition definition;
        @NotNull
        private String description;
        @NotNull
        private OrganizationId organizationId;
        @NotNull
        private SchemaId schemaId;
        @NotNull
        private SchemaVersionId schemaVersionId;
        @NotNull
        private SchemaVersion.Status status;
        @NotNull
        private SchemaVersion.Version version;

        public ContextId getContextId() {
            return this.contextId;
        }

        public SchemaVersion.Definition getDefinition() {
            return this.definition;
        }

        public String getDescription() {
            return this.description;
        }

        public OrganizationId getOrganizationId() {
            return this.organizationId;
        }

        public SchemaId getSchemaId() {
            return this.schemaId;
        }

        public SchemaVersionId getSchemaVersionId() {
            return this.schemaVersionId;
        }

        public SchemaVersion.Status getStatus() {
            return this.status;
        }

        public SchemaVersion.Version getVersion() {
            return this.version;
        }

        public SchemaVersion.State asPublished() {
            return SchemaVersion.this.new State ( this.organizationId, this.contextId, this.schemaId, this.schemaVersionId, this.description, this.definition, this.version, SchemaVersion.Status.Published );
        }

        public SchemaVersion.State withDefinition(@NotNull SchemaVersion.Definition definition) {
            return SchemaVersion.this.new State ( this.organizationId, this.contextId, this.schemaId, this.schemaVersionId, this.description, definition, this.version, this.status );
        }

        public SchemaVersion.State withDescription(@NotNull String description) {
            return SchemaVersion.this.new State ( this.organizationId, this.contextId, this.schemaId, this.schemaVersionId, description, this.definition, this.version, this.status );
        }

        public SchemaVersion.State withVersion(@NotNull SchemaVersion.Version version) {
            return SchemaVersion.this.new State ( this.organizationId, this.contextId, this.schemaId, this.schemaVersionId, this.description, this.definition, version, this.status );
        }

        public State(@NotNull OrganizationId organizationId, @NotNull ContextId contextId, @NotNull SchemaId schemaId, @NotNull SchemaVersionId schemaVersionId, @NotNull String description, @NotNull SchemaVersion.Definition definition, @NotNull SchemaVersion.Version version, @NotNull SchemaVersion.Status status) {
            this.organizationId = organizationId;
            this.contextId = contextId;
            this.schemaId = schemaId;
            this.schemaVersionId = schemaVersionId;
            this.description = description;
            this.definition = definition;
            this.version = version;
            this.status = status;
        }


    }

    /**
     * Inner class of definition of schema version
     */

    public class Definition {
        @NotNull
        private String value;

        @NotNull
        public String getValue() {
            return this.value;
        }

        public Definition(@NotNull String value) {
            super ();
            this.value = value;
        }
    }


    public static enum Status {
        Draft,
        Published,
        Removed,
        Undefined;
    }

    /**
     * Inner class for schema version
     */
    public class Version {
        @NotNull
        private String value;

        @NotNull
        public String getValue() {
            return this.value;
        }

        public Version(@NotNull String value) {
            super ();
            this.value = value;
        }
    }
}