package io.vlingo.schemata.model.context;

import io.vlingo.lattice.model.Source;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Id;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.events.contextevent.ContextDefined;
import io.vlingo.schemata.model.events.contextevent.ContextDescribed;
import io.vlingo.schemata.model.events.contextevent.ContextRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author Chandrabhan Kumhar
 * Used to perform context operations.
 */
public class Context extends EventSourced implements ContextEntity {
    static {
        BiConsumer<Context, ContextDefined> applyContextDefinedFn = Context::applyDefinedVlingoSchemata;
        EventSourced.registerConsumer ( Context.class, ContextDefined.class, applyContextDefinedFn );
        BiConsumer<Context, ContextDescribed> applyContextDescribedFn = Context::applyDescribedVlingoSchemata;
        EventSourced.registerConsumer ( Context.class, ContextDescribed.class, applyContextDescribedFn );
        BiConsumer<Context, ContextRenamed> applyContextNamespaceChangedFn = Context::applyNamespaceChangedVlingoSchemata;
        EventSourced.registerConsumer ( Context.class, ContextRenamed.class, applyContextNamespaceChangedFn );
    }

    private Context.State state;
    private final Result result;

    public Context(final Result result) {
        this.result = result;
        this.apply ( (Source) (new ContextDefined ( Id.OrganizationId.Companion.unique (), Id.ContextId.Companion.unique (), "namespace", "desc" )) );
    }

    /**
     * Used to change the namespace.
     *
     * @param namespace
     */
    @Override
    public void changeNamespaceTo(@NotNull String namespace) {
        boolean var10000 = false;
        if (namespace.length () > 0) {
            var10000 = true;
        }

        boolean var4 = var10000;
        if (!var4) {
            String var3 = "Assertion failed";
            throw new AssertionError ( var3 );
        } else {
            this.apply ( (Source) (new ContextRenamed ( this.state.getOrganizationId (), this.state.getContextId (), namespace )) );
        }
    }

    /**
     * Used to give a description of a context
     *
     * @param description
     */
    @Override
    public void describeAs(@NotNull String description) {
        boolean var10000 = false;
        if (description.length () > 0) {
            var10000 = true;
        }

        boolean var4 = var10000;
        if (!var4) {
            String var3 = "Assertion failed";
            throw new AssertionError ( var3 );
        } else {
            this.apply ( (Source) (new ContextDescribed ( this.state.getOrganizationId (), this.state.getContextId (), description )) );
        }
    }

    /**
     * Used to apply defined context schema
     *
     * @param event
     */
    public void applyDefinedVlingoSchemata(@NotNull ContextDefined event) {
        this.state = new Context.State ( OrganizationId.Companion.existing ( event.getOrganizationId () ), ContextId.Companion.existing ( event.getContextId () ), event.getNamespace (), event.getDescription () );
        result.defined = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply description of context schema
     *
     * @param event
     */
    public void applyDescribedVlingoSchemata(@NotNull ContextDescribed event) {
        this.state = this.state.withDescriptionVlingoSchemata ( event.getDescription () );
        result.described = true;
        result.applied.add ( event );
        result.until.happened ();
    }

    /**
     * Used to apply change namespace of context schema
     *
     * @param event
     */
    public void applyNamespaceChangedVlingoSchemata(@NotNull ContextRenamed event) {
        this.state = this.state.withNamespaceVlingoSchemata ( event.getNamespace () );
        result.renamed = true;
        result.applied.add ( event );
        result.until.happened ();
    }


    /**
     * Inner class for context state
     */
    public class State {
        @NotNull
        private ContextId contextId;
        @NotNull
        private String description;
        @NotNull
        private String namespace;
        @NotNull
        private OrganizationId organizationId;

        public ContextId getContextId() {
            return this.contextId;
        }

        public String getDescription() {
            return this.description;
        }

        public String getNamespace() {
            return this.namespace;
        }

        public OrganizationId getOrganizationId() {
            return this.organizationId;
        }

        public Context.State withDescriptionVlingoSchemata(@NotNull String description) {
            return Context.this.new State ( this.organizationId, this.contextId, this.namespace, description );
        }

        public Context.State withNamespaceVlingoSchemata(@NotNull String namespace) {
            return Context.this.new State ( this.organizationId, this.contextId, namespace, this.description );
        }

        public State(@NotNull OrganizationId organizationId, @NotNull ContextId contextId, @NotNull String namespace, @NotNull String description) {
            this.organizationId = organizationId;
            this.contextId = contextId;
            this.namespace = namespace;
            this.description = description;
        }

    }

}

