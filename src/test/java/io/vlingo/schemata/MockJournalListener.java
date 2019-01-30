// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public final class MockJournalListener implements JournalListener<String> {
  private final List<Entry<String>> entries = new ArrayList<>();
  private final Object lock = new Object();

  public Entry<String> get(final int index) {
    synchronized (lock) {
      return entries.get(index);
    }
  }

  @Override
  public void appended(Entry<String> entry) {
    synchronized (lock) {
      entries.add(entry);
    }
  }

  @Override
  public void appendedWith(Entry<String> entry, State<String> snapshot) {
    synchronized (lock) {
      entries.add(entry);
    }
  }

  @Override
  public void appendedAll(List<Entry<String>> entries) {
    synchronized (lock) {
      entries.addAll(entries);
    }
  }

  @Override
  public void appendedAllWith(List<Entry<String>> entries, State<String> snapshot) {
    synchronized (lock) {
      entries.addAll(entries);
    }
  }
}
