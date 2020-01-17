// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

public class NoopDispatcher<E extends Entry<?>, RS extends State<?>> implements Dispatcher<Dispatchable<E,RS>> {

  @Override
  public void controlWith(final DispatcherControl control) {

  }

  @Override
  public void dispatch(Dispatchable<E, RS> ersDispatchable) {

  }
}
