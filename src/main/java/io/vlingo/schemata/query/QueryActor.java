// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.actors.Actor;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.CompletionSupplier;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.ObjectStoreReader;
import io.vlingo.symbio.store.object.StateObject;

import java.util.function.Supplier;

public abstract class QueryActor<T extends StateObject> extends Actor implements ObjectStoreReader.QueryResultInterest {

    private final ObjectStore objectStore;
    private ObjectStoreReader.QuerySingleResult singleResult;

    protected QueryActor(final ObjectStore objectStore) {
        this.objectStore = objectStore;
    }

    protected <RT> void select(Class<T> stateObjectType, final String sql, Supplier<RT> andThen) {
        objectStore.queryObject(
                MapQueryExpression.using(
                        stateObjectType,
                        sql,
                        MapQueryExpression.map("id", 1L)),
                this, CompletionSupplier.supplierOrNull(andThen, this.completesEventually()));
    }

    @Override
    public void queryObjectResultedIn(final Outcome<StorageException, Result> outcome, final ObjectStoreReader.QuerySingleResult result, final Object object) {
        this.singleResult = result;
    }
}
