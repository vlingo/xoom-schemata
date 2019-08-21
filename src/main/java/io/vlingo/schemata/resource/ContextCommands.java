// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.lattice.model.Command;
import io.vlingo.lattice.router.CommandDispatcher;
import io.vlingo.lattice.router.CommandRouter;
import io.vlingo.lattice.router.CommandRouter.Type;
import io.vlingo.lattice.router.RoutableCommand;
import io.vlingo.schemata.model.Context;
import io.vlingo.schemata.model.ContextEntity;
import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.model.Id.ContextId;

class ContextCommands {
  private final CommandRouter router;
  private final Stage stage;

  ContextCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<Context,Command,ContextState> describeAs(
          final ContextId contextId,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<Context,Command,ContextState> command =
            RoutableCommand
              .speaks(Context.class)
              .to(ContextEntity.class)
              .at(contextId.value)
              .named(Context.nameFrom(contextId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Context,Command,ContextState> moveToNamespace(
          final ContextId contextId,
          final String name) {

    final MoveToNamespace renameTo = new MoveToNamespace(name);

    RoutableCommand<Context,Command,ContextState> command =
            RoutableCommand
              .speaks(Context.class)
              .to(ContextEntity.class)
              .at(contextId.value)
              .named(Context.nameFrom(contextId))
              .delivers(renameTo)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(renameTo);

    router.route(command);

    return command;
  }

  private static class DescribeAs extends Command implements CommandDispatcher<Context,DescribeAs,Completes<ContextState>> {
    private final String description;

    DescribeAs(final String description) {
      this.description = description;
    }

    @Override
    public void accept(final Context protocol, final DescribeAs command, final Completes<ContextState> answer) {
      protocol.describeAs(command.description).andThen(state -> answer.with(state));
    }
  }

  private static class MoveToNamespace extends Command implements CommandDispatcher<Context,MoveToNamespace,Completes<ContextState>> {
    private final String namespace;

    MoveToNamespace(final String namespace) {
      this.namespace = namespace;
    }

    @Override
    public void accept(final Context protocol, final MoveToNamespace command, final Completes<ContextState> answer) {
      protocol.moveToNamespace(command.namespace).andThen(state -> answer.with(state));
    }
  }
}
