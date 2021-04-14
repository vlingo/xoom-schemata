package io.vlingo.xoom.schemata.queries;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CountingStateStore extends InMemoryStateStoreActor {

    private final AtomicInteger readCount = new AtomicInteger(0);

    public CountingStateStore(List list) {
        super(list);
    }

    @Override
    public void read(String id, Class type, ReadResultInterest interest, Object object) {
        readCount.incrementAndGet();
        super.read(id, type, interest, object);
    }

    @Override
    public void readAll(Collection collection, ReadResultInterest interest, Object object) {
        readCount.incrementAndGet();
        super.readAll(collection, interest, object);
    }

    @Override
    public Completes<Stream> streamAllOf(Class stateType) {
        readCount.incrementAndGet();
        return super.streamAllOf(stateType);
    }

    @Override
    public Completes<Stream> streamSomeUsing(QueryExpression query) {
        readCount.incrementAndGet();
        return super.streamSomeUsing(query);
    }

    public int getReadCount(){
        return readCount.get();
    }
}
