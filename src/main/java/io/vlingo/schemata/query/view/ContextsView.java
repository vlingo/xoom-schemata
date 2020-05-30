// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContextsView {
    private final List<Tag> contexts;

    public static ContextsView empty() {
        return new ContextsView();
    }

    private ContextsView() {
        this.contexts = new ArrayList<>();
    }

    private ContextsView(List<Tag> contexts) {
        this.contexts = contexts;
    }

    public ContextsView add(final Tag unit) {
        if (contexts.contains(unit)) {
            return this;
        } else {
            ContextsView result = new ContextsView(new ArrayList<>(contexts));
            result.contexts.add(unit);

            return result;
        }
    }

    public Tag get(final String contextId) {
        Tag context = Tag.only(contextId);

        final int index = contexts.indexOf(context);

        if (index >= 0) {
            context = contexts.get(index);
        }

        return context;
    }

    public ContextsView replace(final Tag context) {
        final int index = contexts.indexOf(context);
        if (index >= 0) {
            ContextsView result = new ContextsView(new ArrayList<>(contexts));
            result.contexts.set(index, context);

            return result;
        } else {
            return this;
        }
    }

    public List<Tag> all() {
        return Collections.unmodifiableList(contexts);
    }

    @Override
    public String toString() {
        return "ContextsView [contexts=" + contexts + "]";
    }
}
