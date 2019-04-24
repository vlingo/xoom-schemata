// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import java.util.List;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public class NoopJournalListener implements JournalListener<String> {
    @Override
    public void appended(Entry<String> entry) {
    }

    @Override
    public void appendedWith(Entry<String> entry, State<String> snapshot) {
    }

    @Override
    public void appendedAll(List<Entry<String>> entries) {
    }

    @Override
    public void appendedAllWith(List<Entry<String>> entries, State<String> snapshot) {
    }
}
