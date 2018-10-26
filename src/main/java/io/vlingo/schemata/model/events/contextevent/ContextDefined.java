package io.vlingo.schemata.model.events.contextevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class ContextDefined extends DomainEvent {
    private String organizationId;
    private String contextId;
    private String namespace;
    private String description;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getDescription() {
        return this.description;
    }

    public ContextDefined(Id.OrganizationId organizationId, Id.ContextId contextId, String namespace, String description) {
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.namespace = namespace;
        this.description = description;
    }
}
