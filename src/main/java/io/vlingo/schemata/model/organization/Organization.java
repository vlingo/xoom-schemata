package io.vlingo.schemata.model.organization;

import io.vlingo.lattice.model.Source;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Id;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.events.organizationevent.OrganizationDefined;
import io.vlingo.schemata.model.events.organizationevent.OrganizationDescribed;
import io.vlingo.schemata.model.events.organizationevent.OrganizationRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author Chandrabhan Kumhar
 * Used to perform Organizational operations.
 */
public class Organization extends EventSourced implements OrganizationEntity {
    static {
        BiConsumer<Organization, OrganizationDefined> applyOrganizationDefinedFn = Organization::applyDefined;
        EventSourced.registerConsumer ( Organization.class, OrganizationDefined.class, applyOrganizationDefinedFn );
        BiConsumer<Organization, OrganizationDescribed> applyOrganizationDescribedFn = Organization::applyDescribed;
        EventSourced.registerConsumer ( Organization.class, OrganizationDescribed.class, applyOrganizationDescribedFn );
        BiConsumer<Organization, OrganizationRenamed> applyOrganizationRenamedFn = Organization::applyRenamed;
        EventSourced.registerConsumer ( Organization.class, OrganizationRenamed.class, applyOrganizationRenamedFn );
    }

    private Organization.State state;
    private final Result result;

    public Organization(final Result result) {
        this.result = result;
        this.apply ( (Source) (new OrganizationDefined ( Id.OrganizationId.Companion.unique (), Id.OrganizationId.Companion.unique (), "organization", "desc" )) );
    }

    /***
     * Used to get description of organization
     * @param description
     */
    @Override
    public void describeAs(@NotNull String description) {

        this.apply ( (Source) (new OrganizationDescribed ( this.state.getId (), description )) );
    }

    /**
     * Used to set rename of on organization
     *
     * @param name
     */
    @Override
    public void renameTo(@NotNull String name) {

        this.apply ( (Source) (new OrganizationRenamed ( this.state.getId (), name )) );
    }

    /**
     * Used to apply defined organization event
     *
     * @param event
     */
    public void applyDefined(@NotNull OrganizationDefined event) {
        this.state = new Organization.State ( OrganizationId.Companion.existing ( event.getOrganizationId () ), OrganizationId.Companion.existing ( event.getParentId () ), event.getName (), event.getDescription () );
        result.defined = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply description event os organization
     *
     * @param event
     */
    public final void applyDescribed(@NotNull OrganizationDescribed event) {
        this.state = this.state.withDescription ( event.getDescription () );
        result.described = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply event to rename an organization
     *
     * @param event
     */
    public final void applyRenamed(@NotNull OrganizationRenamed event) {
        this.state = this.state.withName ( event.getName () );
        result.renamed = true;
        result.applied.add ( event );
        result.until.happened ();
    }


    /**
     * Inner class to define an organization state
     */
    public class State {
        @NotNull
        private OrganizationId id;
        @NotNull
        private OrganizationId parentId;
        @NotNull
        private String name;
        @NotNull
        private String description;

        private Organization.State withDescription(@NotNull String description) {
            return Organization.this.new State ( this.id, this.parentId, this.name, description );
        }

        private Organization.State withName(@NotNull String name) {
            return Organization.this.new State ( this.id, this.parentId, name, this.description );
        }

        public OrganizationId getId() {
            return this.id;
        }

        public OrganizationId getParentId() {
            return this.parentId;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public State(@NotNull OrganizationId id, @NotNull OrganizationId parentId, @NotNull String name, @NotNull String description) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.description = description;
        }

    }
}
