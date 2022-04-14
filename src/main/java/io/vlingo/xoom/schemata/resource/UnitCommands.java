// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.Command;
import io.vlingo.xoom.lattice.router.CommandDispatcher;
import io.vlingo.xoom.lattice.router.CommandRouter;
import io.vlingo.xoom.lattice.router.CommandRouter.Type;
import io.vlingo.xoom.lattice.router.RoutableCommand;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitEntity;
import io.vlingo.xoom.schemata.model.UnitState;

class UnitCommands {
  private final CommandRouter router;
  private final Stage stage;

  UnitCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<Unit,Command,UnitState> describeAs(
          final UnitId unitId,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<Unit,Command,UnitState> command =
            RoutableCommand
              .speaks(Unit.class)
              .to(UnitEntity.class)
              .at(unitId.value)
              .createsWith(Definition.parameters(unitId))
              .named(Unit.nameFrom(unitId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Unit,Command,UnitState> defineWith(
          final UnitId unitId,
          final String name,
          final String description) {

    final DefineWith defineWith = new DefineWith(name, description);

    RoutableCommand<Unit,Command,UnitState> command =
            RoutableCommand
              .speaks(Unit.class)
              .to(UnitEntity.class)
              .at(unitId.value)
              .createsWith(Definition.parameters(unitId))
              .named(Unit.nameFrom(unitId))
              .delivers(defineWith)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(defineWith);

    router.route(command);

    return command;
  }

  RoutableCommand<Unit,Command,UnitState> redefineWith(
          final UnitId unitId,
          final String name,
          final String description) {

    final RedefineWith redefineWith = new RedefineWith(name, description);

    RoutableCommand<Unit,Command,UnitState> command =
            RoutableCommand
              .speaks(Unit.class)
              .to(UnitEntity.class)
              .at(unitId.value)
              .createsWith(Definition.parameters(unitId))
              .named(Unit.nameFrom(unitId))
              .delivers(redefineWith)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(redefineWith);

    router.route(command);

    return command;
  }

  RoutableCommand<Unit,Command,UnitState> renameTo(
          final UnitId unitId,
          final String name) {

    final RenameTo renameTo = new RenameTo(name);

    RoutableCommand<Unit,Command,UnitState> command =
            RoutableCommand
              .speaks(Unit.class)
              .to(UnitEntity.class)
              .at(unitId.value)
              .createsWith(Definition.parameters(unitId))
              .named(Unit.nameFrom(unitId))
              .delivers(renameTo)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(renameTo);

    router.route(command);

    return command;
  }

  private static class DefineWith extends Command implements CommandDispatcher<Unit,DefineWith,Completes<UnitState>> {
    private final String description;
    private final String name;

    DefineWith(final String name, final String description) {
      this.name = name;
      this.description = description;
    }

    @Override
    public void accept(final Unit protocol, final DefineWith command, final Completes<UnitState> answer) {
      protocol
        .defineWith(command.name, command.description)
        .andThen(state -> answer.with(state))
        .otherwise(state -> answer.otherwise((UnitState none) -> none));
    }
  }

  private static class DescribeAs extends Command implements CommandDispatcher<Unit,DescribeAs,Completes<UnitState>> {
    private final String description;

    DescribeAs(final String description) {
      this.description = description;
    }

    @Override
    public void accept(final Unit protocol, final DescribeAs command, final Completes<UnitState> answer) {
      protocol.describeAs(command.description).andThen(state -> answer.with(state));
    }
  }

  private static class RedefineWith extends Command implements CommandDispatcher<Unit,RedefineWith,Completes<UnitState>> {
    private final String description;
    private final String name;

    RedefineWith(final String name, final String description) {
      this.name = name;
      this.description = description;
    }

    @Override
    public void accept(final Unit protocol, final RedefineWith command, final Completes<UnitState> answer) {
      protocol.redefineWith(command.name, command.description).andThen(state -> answer.with(state));
    }
  }

  private static class RenameTo extends Command implements CommandDispatcher<Unit,RenameTo,Completes<UnitState>> {
    private final String name;

    RenameTo(final String name) {
      this.name = name;
    }

    @Override
    public void accept(final Unit protocol, final RenameTo command, final Completes<UnitState> answer) {
      protocol.renameTo(command.name).andThen(state -> answer.with(state));
    }
  }
}
