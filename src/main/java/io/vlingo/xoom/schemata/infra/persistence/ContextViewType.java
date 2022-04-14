// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.lattice.model.DomainEvent;

public enum ContextViewType {
    UnitDefined,
    ContextDefined,
    ContextDescribed,
    ContextRedefined,
    ContextMovedToNamespace,

    Unmatched;

    public static ContextViewType match(final DomainEvent event) {
        try {
            return ContextViewType.valueOf(event.typeName());
        } catch (Exception e) {
            return ContextViewType.Unmatched;
        }
    }
}
