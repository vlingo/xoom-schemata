package io.vlingo.xoom.schemata.queries;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.StateStoreEntryReader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingStateStore implements StateStore {

    private final AtomicInteger readCount = new AtomicInteger(0);
    private final StateStore delegate;

    public CountingStateStore(StateStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public void read(String id, Class type, ReadResultInterest interest, Object object) {
        readCount.incrementAndGet();
        delegate.read(id, type, interest, object);
    }

    @Override
    public void readAll(Collection collection, ReadResultInterest interest, Object object) {
        readCount.incrementAndGet();
        delegate.readAll(collection, interest, object);
    }

    @Override
    public Completes<Stream> streamAllOf(Class stateType) {
        readCount.incrementAndGet();
        return delegate.streamAllOf(stateType);
    }

    @Override
    public Completes<Stream> streamSomeUsing(QueryExpression query) {
        readCount.incrementAndGet();
        return delegate.streamSomeUsing(query);
    }

    @Override
    public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(String s) {
        return delegate.entryReader(s);
    }

    @Override
    public <S, C> void write(String s, S s1, int i, List<Source<C>> list, Metadata metadata, WriteResultInterest writeResultInterest, Object o) {
        delegate.write(s, s1, i, list, metadata, writeResultInterest, o);
    }

    public int getReadCount(){
        return readCount.get();
    }
}
