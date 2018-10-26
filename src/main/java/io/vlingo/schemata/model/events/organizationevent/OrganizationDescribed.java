package io.vlingo.schemata.model.events.organizationevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class OrganizationDescribed extends DomainEvent {
    private String organizationId;
    private String description;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getDescription() {
        return this.description;
    }

    public OrganizationDescribed(Id.OrganizationId organizationId, String description) {
        this.organizationId = organizationId.getValue ();
        this.description = description;
    }
}
