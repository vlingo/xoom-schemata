// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata;

import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;

public class NoopDispatcher<E extends Entry<?>, RS extends State<?>> implements Dispatcher<Dispatchable<E,RS>> {

  @Override
  public void controlWith(final DispatcherControl control) {

  }

  @Override
  public void dispatch(Dispatchable<E, RS> ersDispatchable) {

  }
}
