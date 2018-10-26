package io.vlingo.schemata.model.events.organizationevent;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id;

/**
 * @author Chandrabhan Kumhar
 */
public class OrganizationDefined extends DomainEvent {
    private String organizationId;
    private String parentId;
    private String name;
    private String description;

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public OrganizationDefined(Id.OrganizationId organizationId, Id.OrganizationId parentId, String name, String description) {
        this.organizationId = organizationId.getValue ();
        this.parentId = parentId.getValue ();
        this.name = name;
        this.description = description;
    }
}
