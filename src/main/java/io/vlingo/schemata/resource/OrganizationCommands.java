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
import io.vlingo.schemata.model.Organization;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.model.OrganizationState;

class OrganizationCommands {
  private final CommandRouter router;
  private final Stage stage;

  OrganizationCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<Organization,Command,OrganizationState> describeAs(
          final String address,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<Organization,Command,OrganizationState> command =
            RoutableCommand
              .speaks(Organization.class)
              .to(OrganizationEntity.class)
              .at(address)
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Organization,Command,OrganizationState> renameTo(
          final String address,
          final String name) {

    final RenameTo renameTo = new RenameTo(name);

    RoutableCommand<Organization,Command,OrganizationState> command =
            RoutableCommand
              .speaks(Organization.class)
              .to(OrganizationEntity.class)
              .at(address)
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
