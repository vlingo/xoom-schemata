package io.vlingo.schemata;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

import java.util.List;

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
