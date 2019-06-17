// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public class MockJournalListener implements JournalListener<String> {
  private AccessSafely access = AccessSafely.afterCompleting(0);

  private final List<Source<?>> sources;
  private final EntryAdapterProvider entryAdapterProvider;

  public MockJournalListener(final EntryAdapterProvider entryAdapterProvider) {
    this.entryAdapterProvider = entryAdapterProvider;
    this.sources = new ArrayList<>();
  }

  @Override
  public void appended(final Entry<String> entry) {
    access.writeUsing("entry", entry);
  }

  @Override
  public void appendedWith(final Entry<String> entry, final State<String> snapshot) {
    access.writeUsing("entry", entry);
  }

  @Override
  public void appendedAll(final List<Entry<String>> entries) {
    access.writeUsing("entries", entries);
  }

  @Override
  public void appendedAllWith(final List<Entry<String>> entries, final State<String> snapshot) {
    access.writeUsing("entries", entries);
  }

  public AccessSafely afterCompleting(final int happenings) {
    access = AccessSafely.afterCompleting(happenings);

    access.writingWith("entries", (List<Entry<String>> all) -> sources.addAll(entryAdapterProvider.asSources(all)));
    access.writingWith("entry", (Entry<String> one) -> sources.add(entryAdapterProvider.asSource(one)));
    access.readingWith("entries", () -> sources);

    return access;
  }
}
