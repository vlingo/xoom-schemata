// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.http.Response;
import io.vlingo.schemata.Bootstrap;
import io.vlingo.schemata.XoomInitializer;
import io.vlingo.schemata.resource.TestResponseChannelConsumer.Progress;
import io.vlingo.wire.channel.ResponseChannelConsumer;
import io.vlingo.wire.fdx.bidirectional.BasicClientRequestResponseChannel;
import io.vlingo.wire.fdx.bidirectional.ClientRequestResponseChannel;
import io.vlingo.wire.message.ByteBufferAllocator;
import io.vlingo.wire.message.Converters;
import io.vlingo.wire.node.Address;
import io.vlingo.wire.node.AddressType;
import io.vlingo.wire.node.Host;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class UiResourceTest {
  private Bootstrap bootstrap;
  private final ByteBuffer buffer = ByteBufferAllocator.allocate(100);
  private ClientRequestResponseChannel client;
  private ResponseChannelConsumer consumer;
  private Progress progress;
  private World world;

  @Test
  public void testThatRootRequestRedirects() {
    client.requestWith(toByteBuffer("GET / HTTP/1.1\nHost: vlingo.io\n\n"));

    final AccessSafely moreConsumeCalls = progress.expectConsumeTimes(1);
    while (moreConsumeCalls.totalWrites() < 1) {
      client.probeChannel();
    }
    moreConsumeCalls.readFrom("completed");

    final Response getResponse = progress.responses.poll();

    assertEquals(1, progress.consumeCount.get());
    assertEquals(Response.Status.MovedPermanently, getResponse.status);
  }

  @Before
  public void setUp() throws Exception {
    XoomInitializer.main(new String[]{"test"});
    progress = new Progress();
    world = XoomInitializer.instance().world;
    consumer = world.actorFor(ResponseChannelConsumer.class, Definition.has(TestResponseChannelConsumer.class, Definition.parameters(progress)));
    client = new BasicClientRequestResponseChannel(Address.from(Host.of("localhost"), 19090, AddressType.NONE), consumer, 100, 10240, world.defaultLogger());
  }

  @After
  public void tearDown() {
    client.close();
    XoomInitializer.instance().server.shutDown();
    XoomInitializer.instance().world.terminate();
  }

  private ByteBuffer toByteBuffer(final String requestContent) {
    buffer.clear();
    buffer.put(Converters.textToBytes(requestContent));
    buffer.flip();
    return buffer;
  }
}
