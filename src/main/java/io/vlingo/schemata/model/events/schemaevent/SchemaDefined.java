package io.vlingo.schemata.model.events.schemaevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.EventsCategory;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class SchemaDefined extends DomainEvent {

    private String organizationId;
    private String contextId;
    private String schemaId;
    private String category;
    private String name;
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

    public String getCategory() {
        return this.category;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public SchemaDefined(Id organizationId, Id.ContextId contextId, Id.SchemaId schemaId, EventsCategory.Category category, String name, String description) {
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.schemaId = schemaId.getValue ();
        this.category = category.name ();
        this.name = name;
        this.description = description;
    }
}
