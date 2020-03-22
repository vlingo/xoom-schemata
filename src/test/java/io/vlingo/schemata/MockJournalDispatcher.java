// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

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
