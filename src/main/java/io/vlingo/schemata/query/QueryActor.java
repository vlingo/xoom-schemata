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
    private final ObjectStoreReader.QueryResultInterest queryResultInterest = this.selfAs(ObjectStoreReader.QueryResultInterest.class);
    private ObjectStoreReader.QuerySingleResult singleResult;

    protected QueryActor(final ObjectStore objectStore) {
        this.objectStore = objectStore;
    }

    @SuppressWarnings({ "unchecked" })
    protected <RT> T select(Class<T> stateObjectType, final String sql, Supplier<RT> andThen) {
        objectStore.queryObject(
                MapQueryExpression.using(
                        stateObjectType,
                        sql,
                        MapQueryExpression.map("id", 1L)),
                this.queryResultInterest, CompletionSupplier.supplierOrNull(andThen, this.completesEventually()));
        return (T)this.singleResult.stateObject;
    }

    @Override
    public final void queryObjectResultedIn(Outcome<StorageException, Result> outcome, ObjectStoreReader.QuerySingleResult queryResult, Object object) {
        this.singleResult = queryResult;
    }

    @Override
    public final void queryAllResultedIn(Outcome<StorageException, Result> outcome, ObjectStoreReader.QueryMultiResults results, Object object) {
        // has to be implemented.
        throw new UnsupportedOperationException("Must be unreachable: queryAllResultedIn()");
    }
}
