// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Tuple4;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.query.view.NamedSchemaView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.schemata.query.view.SchemasView;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class SchemaQueriesActor extends StateStoreQueryActor implements SchemaQueries {
    public SchemaQueriesActor(StateStore stateStore) {
        super(stateStore);
    }


    @Override
    public Completes<SchemasView> schemas(String organizationId, String unitId, String contextId) {
        return queryStateFor(contextId, SchemasView.class);
    }

    @Override
    public Completes<SchemaView> schema(String organizationId, String unitId, String contextId, String schemaId) {
        final String id = dataIdFrom(":", contextId, schemaId);
        return queryStateFor(id, SchemaView.class);
    }

    @Override
    public Completes<NamedSchemaView> schemaByNames(String organization, String unit, String context, String schema) {
        Path path = Path.with(organization, unit, context, schema);
        String reference = path.toReference();
        return queryStateFor(reference, NamedSchemaView.class);
    }

    @Override
    public Completes<NamedSchemaView> schemaByNamesWithRetries(String organization, String unit, String context,
                                                               String schema, long retryInterval, int retryCount) {
        return tryQuery(Tuple4.from(organization, unit, context, schema), completes(), retryInterval, retryCount);
    }

    private Completes<NamedSchemaView> tryQuery(Tuple4<String, String, String, String> query,
                                                Completes<NamedSchemaView> completes, long retryInterval, int remainingRetries) {
        schemaByNames(query._1, query._2, query._3, query._4).andThenConsume(result -> {
            if (result == null) {
                if (remainingRetries > 0) {
                    scheduler().scheduleOnce(
                            (scheduled, data) -> tryQuery(data._1, data._2, data._3, data._4),
                            Tuple4.from(query, completes, retryInterval, remainingRetries - 1),
                            0,
                            retryInterval
                    );
                } else {
                    completes.with(NamedSchemaView.empty());
                }
            } else {
                completes.with(result);
            }
        });
        return completes;
    }
}
