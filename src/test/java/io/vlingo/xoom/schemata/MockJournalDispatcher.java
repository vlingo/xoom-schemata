// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;

import java.util.ArrayList;
import java.util.List;

public class MockJournalDispatcher implements Dispatcher<Dispatchable<Entry<String>, State<?>>> {
  private AccessSafely access = AccessSafely.afterCompleting(0);

  private final List<Source<?>> sources;
  private final EntryAdapterProvider entryAdapterProvider;

  public MockJournalDispatcher(final EntryAdapterProvider entryAdapterProvider) {
    this.entryAdapterProvider = entryAdapterProvider;
    this.sources = new ArrayList<>();
  }

  public AccessSafely afterCompleting(final int happenings) {
    access = AccessSafely.afterCompleting(happenings);

    access.writingWith("entries", (List<Entry<String>> all) -> sources.addAll(entryAdapterProvider.asSources(all)));
    access.writingWith("entry", (Entry<String> one) -> sources.add(entryAdapterProvider.asSource(one)));
    access.readingWith("entries", () -> sources);

    return access;
  }

  @Override
  public void controlWith(final DispatcherControl control) {

  }

  @Override
  public void dispatch(final Dispatchable<Entry<String>, State<?>> dispatchable) {
    access.writeUsing("entries", dispatchable.entries());
  }
}
