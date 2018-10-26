package io.vlingo.schemata.model.events.schemaevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class SchemaRenamed extends DomainEvent {

    private String organizationId;
    private String contextId;
    private String schemaId;
    private String name;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public String getSchemaId() {
        return this.schemaId;
    }

    public String getName() {
        return this.name;
    }

    public SchemaRenamed(Id organizationId, Id.ContextId contextId, Id.SchemaId schemaId, String name) {
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.schemaId = schemaId.getValue ();
        this.name = name;
    }
}
