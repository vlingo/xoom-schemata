package io.vlingo.schemata.model.schema;

import io.vlingo.lattice.model.Source;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.EventsCategory;
import io.vlingo.schemata.model.Id;
import io.vlingo.schemata.model.events.schemaevent.SchemaDefined;
import io.vlingo.schemata.model.events.schemaevent.SchemaDescribed;
import io.vlingo.schemata.model.events.schemaevent.SchemaRecategorized;
import io.vlingo.schemata.model.events.schemaevent.SchemaRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author Chandrabhan Kumhar
 * Used to perform schema operations
 */
public class Schema extends EventSourced implements SchemaEntity {

    static {
        BiConsumer<Schema, SchemaDefined> applySchemaDefinedFn = Schema::applyDefined;
        EventSourced.registerConsumer ( Schema.class, SchemaDefined.class, applySchemaDefinedFn );
        BiConsumer<Schema, SchemaRecategorized> applySchemaRecategorizedFn = Schema::applyRecategorized;
        EventSourced.registerConsumer ( Schema.class, SchemaRecategorized.class, applySchemaRecategorizedFn );
        BiConsumer<Schema, SchemaDescribed> applySchemaDescribedFn = Schema::applyDescribed;
        EventSourced.registerConsumer ( Schema.class, SchemaDescribed.class, applySchemaDescribedFn );
        BiConsumer<Schema, SchemaRenamed> applySchemaRenamedFn = Schema::applyRenamed;
        EventSourced.registerConsumer ( Schema.class, SchemaRenamed.class, applySchemaRenamedFn );
    }

    private Schema.State state;
    private final Result result;

    public Schema(final Result result) {
        this.result = result;
        this.apply ( (Source) (new SchemaDefined ( Id.OrganizationId.Companion.unique (), Id.ContextId.Companion.unique (), Id.SchemaId.Companion.unique (), EventsCategory.Category.None, "name", "desc" )) );
    }

    /**
     * Used to set description of a schema
     *
     * @param description
     */
    @Override
    public void describeAs(@NotNull String description) {
        this.apply ( (Source) (new SchemaDescribed ( this.state.getOrganizationId (), this.state.getContextId (), this.state.getSchemaId (), description )) );
    }

    /**
     * Used to recategorized a category of schema
     *
     * @param category
     */
    @Override
    public void recategorizedAs(@NotNull EventsCategory.Category category) {
        this.apply ( (Source) (new SchemaRecategorized ( this.state.getOrganizationId (), this.state.getContextId (), this.state.getSchemaId (), category )) );
    }

    /**
     * Used to rename a schema
     *
     * @param name
     */
    @Override
    public void renameTo(@NotNull String name) {
        this.apply ( (Source) (new SchemaRenamed ( this.state.getOrganizationId (), this.state.getContextId (), this.state.getSchemaId (), name )) );
    }

    /**
     * Used to apply defined schema event
     *
     * @param event
     */
    public void applyDefined(@NotNull SchemaDefined event) {
        this.state = new Schema.State ( Id.OrganizationId.Companion.existing ( event.getOrganizationId () ), Id.ContextId.Companion.existing ( event.getContextId () ), Id.SchemaId.Companion.existing ( event.getSchemaId () ), EventsCategory.Category.None, event.getName (), event.getDescription () );
        result.defined = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply description event of a schema
     *
     * @param event
     */
    public void applyDescribed(@NotNull SchemaDescribed event) {
        this.state = this.state.withDescription ( event.getDescription () );
        result.described = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply event for recategorization of a schema category
     *
     * @param event
     */
    public void applyRecategorized(@NotNull SchemaRecategorized event) {
        this.state = this.state.withCategory ( EventsCategory.Category.valueOf ( event.getCategory () ) );
        result.recategorised = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply event to rename a schema
     *
     * @param event
     */
    public void applyRenamed(@NotNull SchemaRenamed event) {
        this.state = this.state.withName ( event.getName () );
        result.renamed = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Inner class of a schema state
     */
    public class State {
        @NotNull
        private EventsCategory.Category category;
        @NotNull
        private Id.ContextId contextId;
        @NotNull
        private String description;
        @NotNull
        private String name;
        @NotNull
        private Id.OrganizationId organizationId;
        @NotNull
        private Id.SchemaId schemaId;

        public EventsCategory.Category getCategory() {
            return this.category;
        }

        public Id.ContextId getContextId() {
            return this.contextId;
        }

        public String getDescription() {
            return this.description;
        }

        public String getName() {
            return this.name;
        }

        public Id.OrganizationId getOrganizationId() {
            return this.organizationId;
        }

        public Id.SchemaId getSchemaId() {
            return this.schemaId;
        }

        public Schema.State withCategory(@NotNull EventsCategory.Category category) {
            return Schema.this.new State ( this.organizationId, this.contextId, this.schemaId, category, this.name, this.description );
        }

        public Schema.State withDescription(@NotNull String description) {
            return Schema.this.new State ( this.organizationId, this.contextId, this.schemaId, this.category, this.name, description );
        }

        public Schema.State withName(@NotNull String name) {
            return Schema.this.new State ( this.organizationId, this.contextId, this.schemaId, this.category, name, this.description );
        }

        public State(Id.OrganizationId organizationId, Id.ContextId contextId, Id.SchemaId schemaId, EventsCategory.Category category, String name, String description) {
            this.organizationId = organizationId;
            this.contextId = contextId;
            this.schemaId = schemaId;
            this.category = category;
            this.name = name;
            this.description = description;
        }


    }


}
