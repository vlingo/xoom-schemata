// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

public class ContextView extends View {
    private final String contextId;
    private final String namespace;
    private final String description;

    public static ContextView empty() {
        return new ContextView();
    }

    public static ContextView with(String contextId) {
        return new ContextView(contextId);
    }

    public static ContextView with(String contextId, String namespace, String description) {
        return new ContextView(contextId, namespace, description);
    }

    private ContextView() {
        this("", "", "");
    }

    private ContextView(String contextId) {
        this(contextId, "", "");
    }

    private ContextView(String contextId, String namespace, String description) {
        this.contextId = contextId;
        this.namespace = namespace;
        this.description = description;
    }

    public String contextId() {
        return contextId;
    }

    public String namespace() {
        return namespace;
    }

    public String description() {
        return description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((contextId == null) ? 0 : contextId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        return contextId.equals(((ContextView) other).contextId);
    }

    public ContextView mergeNamespaceWith(String contextId, String namespace) {
        if (this.contextId.equals(contextId)) {
            return new ContextView(this.contextId, namespace, this.description);
        } else {
            return this;
        }
    }

    public ContextView mergeDescriptionWith(String contextId, String description) {
        if (this.contextId.equals(contextId)) {
            return new ContextView(this.contextId, this.namespace, description);
        } else {
            return this;
        }
    }

    public ContextView mergeWith(String contextId, String namespace, String description) {
        if (this.contextId.equals(contextId)) {
            return new ContextView(this.contextId, namespace, description);
        } else {
            return this;
        }
    }

    @Override
    public String toString() {
        return "ContextView [contextId=" + contextId + ", namespace=" + namespace + ", description=" + description + "]";
    }
}
