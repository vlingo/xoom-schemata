// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;

public class DependencyRecorderActor extends Actor implements DependencyRecorder {

  public DependencyRecorderActor() {

  }

  @Override
  public Completes<Boolean> recordDependency(final Dependency dependency) {
    return null;
  }
}
