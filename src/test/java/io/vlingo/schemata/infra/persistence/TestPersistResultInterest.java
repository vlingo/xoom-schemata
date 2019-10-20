// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.common.Outcome;
import io.vlingo.common.Tuple2;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStoreWriter;

public class TestPersistResultInterest implements ObjectStoreWriter.PersistResultInterest {
    private AccessSafely access = AccessSafely.afterCompleting(0);
    private AtomicReference<Outcome<StorageException, Result>> outcome = new AtomicReference<>();
    private AtomicReference<Object> persistentObject = new AtomicReference<>();

    @Override
    public void persistResultedIn(final Outcome<StorageException, Result> outcome, final Object persistentObject, final int possible, final int actual, final Object object) {
        this.outcome.set(outcome);
        access.writeUsing("outcome", Tuple2.from(outcome, persistentObject));
    }

    public AccessSafely afterCompleting(final int times) {
        access = AccessSafely.afterCompleting(times);

        access.writingWith("outcome", (Tuple2<Outcome<StorageException, Result>,Object> o) -> { outcome.set(o._1); persistentObject.set(o._2); });
        access.readingWith("outcome", () -> outcome.get());
        access.readingWith("persistentObject", () -> persistentObject.get());

        return access;
    }
}
