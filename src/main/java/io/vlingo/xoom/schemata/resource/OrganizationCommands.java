// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationEntity;
import io.vlingo.xoom.schemata.model.OrganizationState;

class OrganizationCommands {
  private final CommandRouter router;
  private final Stage stage;

  OrganizationCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<Organization,Command,OrganizationState> describeAs(
          final OrganizationId organizationId,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<Organization,Command,OrganizationState> command =
            RoutableCommand
              .speaks(Organization.class)
              .to(OrganizationEntity.class)
              .at(organizationId.value)
              .createsWith(Definition.parameters(organizationId))
              .named(Organization.nameFrom(organizationId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Organization,Command,OrganizationState> redefineWith(
          final OrganizationId organizationId,
          final String name,
          final String description) {

    final RedefineWith redefineWith = new RedefineWith(name, description);

    RoutableCommand<Organization,Command,OrganizationState> command =
            RoutableCommand
              .speaks(Organization.class)
              .to(OrganizationEntity.class)
              .at(organizationId.value)
              .createsWith(Definition.parameters(organizationId))
              .named(Organization.nameFrom(organizationId))
              .delivers(redefineWith)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(redefineWith);

    router.route(command);

    return command;
  }

  RoutableCommand<Organization,Command,OrganizationState> renameTo(
          final OrganizationId organizationId,
          final String name) {

    final RenameTo renameTo = new RenameTo(name);

    RoutableCommand<Organization,Command,OrganizationState> command =
            RoutableCommand
              .speaks(Organization.class)
              .to(OrganizationEntity.class)
              .at(organizationId.value)
              .createsWith(Definition.parameters(organizationId))
              .named(Organization.nameFrom(organizationId))
              .delivers(renameTo)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(renameTo);

    router.route(command);

    return command;
  }

  private static class DescribeAs extends Command implements CommandDispatcher<Organization,DescribeAs,Completes<OrganizationState>> {
    private final String description;

    DescribeAs(final String description) {
      this.description = description;
    }

    @Override
    public void accept(final Organization protocol, final DescribeAs command, final Completes<OrganizationState> answer) {
      protocol.describeAs(command.description).andThen(state -> answer.with(state));
    }
  }

  private static class RedefineWith extends Command implements CommandDispatcher<Organization,RedefineWith,Completes<OrganizationState>> {
    private final String description;
    private final String name;

    RedefineWith(final String name, final String description) {
      this.name = name;
      this.description = description;
    }

    @Override
    public void accept(final Organization protocol, final RedefineWith command, final Completes<OrganizationState> answer) {
      protocol.redefineWith(command.name, command.description).andThen(state -> answer.with(state));
    }
  }

  private static class RenameTo extends Command implements CommandDispatcher<Organization,RenameTo,Completes<OrganizationState>> {
    private final String name;

    RenameTo(final String name) {
      this.name = name;
    }

    @Override
    public void accept(final Organization protocol, final RenameTo command, final Completes<OrganizationState> answer) {
      protocol.renameTo(command.name).andThen(state -> answer.with(state));
    }
  }
}
