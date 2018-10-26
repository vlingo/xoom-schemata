package io.vlingo.schemata.model.events.schemaevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class SchemaDescribed extends DomainEvent {
    private String organizationId;
    private String contextId;
    private String schemaId;
    private String description;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public String getSchemaId() {
        return this.schemaId;
    }

    public String getDescription() {
        return this.description;
    }

    public SchemaDescribed(Id organizationId, Id.ContextId contextId, Id.SchemaId schemaId, String description) {
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.schemaId = schemaId.getValue ();
        this.description = description;
    }
}
