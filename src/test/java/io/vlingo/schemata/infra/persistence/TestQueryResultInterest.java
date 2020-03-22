// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStoreReader;

public class TestQueryResultInterest implements ObjectStoreReader.QueryResultInterest {
    private AtomicReference<ObjectStoreReader.QueryMultiResults> multiResults = new AtomicReference<>();
    private AtomicReference<ObjectStoreReader.QuerySingleResult> singleResult = new AtomicReference<>();
    public TestUntil until;

    @Override
    public void queryAllResultedIn(final Outcome<StorageException, Result> outcome, final ObjectStoreReader.QueryMultiResults results, final Object object) {
      synchronized (this) {
        multiResults.set(results);
        until.happened();
      }
    }

    @Override
    public void queryObjectResultedIn(final Outcome<StorageException, Result> outcome, final ObjectStoreReader.QuerySingleResult result, final Object object) {
      synchronized (this) {
        singleResult.set(result);
        until.happened();
      }
    }

    public ObjectStoreReader.QueryMultiResults multi() {
      synchronized (this) {
        return multiResults.get();
      }
    }

    public ObjectStoreReader.QuerySingleResult single() {
      synchronized (this) {
        return singleResult.get();
      }
    }
}
