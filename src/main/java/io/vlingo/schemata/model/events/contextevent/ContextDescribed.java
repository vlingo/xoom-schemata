package io.vlingo.schemata.model.events.contextevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class ContextDescribed extends DomainEvent {
    private String organizationId;
    private String contextId;
    private String description;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getContextId() {
        return this.contextId;
    }

    public String getDescription() {
        return this.description;
    }

    public ContextDescribed(Id.OrganizationId organizationId, Id.ContextId contextId, String description) {
        this.organizationId = organizationId.getValue ();
        this.contextId = contextId.getValue ();
        this.description = description;
    }
}
