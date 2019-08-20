// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStoreReader;

import java.util.concurrent.atomic.AtomicReference;

public class TestQueryResultInterest implements ObjectStoreReader.QueryResultInterest {
    public AtomicReference<ObjectStoreReader.QueryMultiResults> multiResults = new AtomicReference<>();
    public AtomicReference<ObjectStoreReader.QuerySingleResult> singleResult = new AtomicReference<>();
    public TestUntil until;

    @Override
    public void queryAllResultedIn(final Outcome<StorageException, Result> outcome, final ObjectStoreReader.QueryMultiResults results, final Object object) {
        multiResults.set(results);
        until.happened();
    }

    @Override
    public void queryObjectResultedIn(final Outcome<StorageException, Result> outcome, final ObjectStoreReader.QuerySingleResult result, final Object object) {
        singleResult.set(result);
        until.happened();
    }
}
