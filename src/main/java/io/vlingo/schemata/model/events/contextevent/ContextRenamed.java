package io.vlingo.schemata.model.events.contextevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class ContextRenamed extends DomainEvent {
    private String organizationId;
    private String contextId;
    private String namespace;
    private String name;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public ContextRenamed(Id.OrganizationId organizationId, Id.ContextId contextId, String name) {
        this.name = name;
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.namespace = this.name;
    }
}
