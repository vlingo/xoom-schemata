// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.schemata.query.view.OrganizationView;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapter;

public class OrganizationStateAdapter implements StateAdapter<OrganizationView, State.TextState> {
    @Override
    public int typeVersion() {
        return 0;
    }

    @Override
    public OrganizationView fromRawState(State.TextState textState) {
        return JsonSerialization.deserialized(textState.data, textState.typed());
    }

    @Override
    public <ST> ST fromRawState(State.TextState textState, Class<ST> aClass) {
        return JsonSerialization.deserialized(textState.data, aClass);
    }

    @Override
    public State.TextState toRawState(String id, OrganizationView state, int stateVersion, Metadata metadata) {
        final String serialization = JsonSerialization.serialized(state);
        return new State.TextState(id, OrganizationView.class, typeVersion(), serialization, stateVersion, metadata);
    }
}