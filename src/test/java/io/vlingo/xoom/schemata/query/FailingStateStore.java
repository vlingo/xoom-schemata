// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Failure;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.StateStoreEntryReader;

public class FailingStateStore implements StateStore {

  private final StateStore delegate;
  private final AtomicInteger readCount = new AtomicInteger(0);
  private final AtomicInteger expectedReadFailures = new AtomicInteger(0);

  public FailingStateStore(final StateStore delegate) {
    this.delegate = delegate;
  }

  @Override
  public void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
    if (readCount.incrementAndGet() > expectedReadFailures.get()) {
      delegate.read(id, type, interest, object);
    } else {
      interest.readResultedIn(Failure.of(new StorageException(Result.NotFound, "Not found.")), id, null, -1, null, object);
    }
  }

  @Override
  public void readAll(Collection<TypedStateBundle> bundles, ReadResultInterest interest, Object object) {
    readCount.incrementAndGet();
    delegate.readAll(bundles, interest, object);
  }

  @Override
  public Completes<Stream> streamAllOf(final Class<?> stateType) {
    readCount.incrementAndGet();
    return delegate.streamAllOf(stateType);
  }

  @Override
  public Completes<Stream> streamSomeUsing(final QueryExpression query) {
    readCount.incrementAndGet();
    return delegate.streamSomeUsing(query);
  }

  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String s) {
    return delegate.entryReader(s);
  }

  @Override
  public <S, C> void write(final String s, final S s1, final int i, final List<Source<C>> list, final Metadata metadata, final WriteResultInterest writeResultInterest, final Object o) {
    delegate.write(s, s1, i, list, metadata, writeResultInterest, o);
  }

  public void expectReadFailures(final Integer count) {
    expectedReadFailures.set(count);
  }
}
