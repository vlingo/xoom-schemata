package io.vlingo.schemata.model.events.organizationevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class OrganizationRenamed extends DomainEvent {
    private String organizationId;
    private String name;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getName() {
        return this.name;
    }

    public OrganizationRenamed(Id.OrganizationId organizationId, String name) {
        this.organizationId = organizationId.getValue ();
        this.name = name;
    }
}
