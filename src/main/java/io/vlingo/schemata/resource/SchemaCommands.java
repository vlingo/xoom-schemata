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
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Schema;
import io.vlingo.schemata.model.SchemaEntity;
import io.vlingo.schemata.model.SchemaState;

class SchemaCommands {
  private final CommandRouter router;
  private final Stage stage;

  SchemaCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<Schema,Command,SchemaState> categorizeAs(
          final SchemaId schemaId,
          final Category category) {

    final CategorizeAs categorizedAs = new CategorizeAs(category);

    RoutableCommand<Schema,Command,SchemaState> command =
            RoutableCommand
              .speaks(Schema.class)
              .to(SchemaEntity.class)
              .at(schemaId.value)
              .named(Schema.nameFrom(schemaId))
              .delivers(categorizedAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(categorizedAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Schema,Command,SchemaState> describeAs(
          final SchemaId schemaId,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<Schema,Command,SchemaState> command =
            RoutableCommand
              .speaks(Schema.class)
              .to(SchemaEntity.class)
              .at(schemaId.value)
              .named(Schema.nameFrom(schemaId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Schema,Command,SchemaState> renameTo(
          final SchemaId schemaId,
          final String name) {

    final RenameTo renameTo = new RenameTo(name);

    RoutableCommand<Schema,Command,SchemaState> command =
            RoutableCommand
              .speaks(Schema.class)
              .to(SchemaEntity.class)
              .at(schemaId.value)
              .named(Schema.nameFrom(schemaId))
              .delivers(renameTo)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(renameTo);

    router.route(command);

    return command;
  }

  private static class DescribeAs extends Command implements CommandDispatcher<Schema,DescribeAs,Completes<SchemaState>> {
    private final String description;

    DescribeAs(final String description) {
      this.description = description;
    }

    @Override
    public void accept(final Schema protocol, final DescribeAs command, final Completes<SchemaState> answer) {
      protocol.describeAs(command.description).andThen(state -> answer.with(state));
    }
  }

  private static class CategorizeAs extends Command implements CommandDispatcher<Schema,CategorizeAs,Completes<SchemaState>> {
    private final Category category;

    CategorizeAs(final Category category) {
      this.category = category;
    }

    @Override
    public void accept(final Schema protocol, final CategorizeAs command, final Completes<SchemaState> answer) {
      protocol.categorizeAs(command.category).andThen(state -> answer.with(state));
    }
  }

  private static class RenameTo extends Command implements CommandDispatcher<Schema,RenameTo,Completes<SchemaState>> {
    private final String namespace;

    RenameTo(final String namespace) {
      this.namespace = namespace;
    }

    @Override
    public void accept(final Schema protocol, final RenameTo command, final Completes<SchemaState> answer) {
      protocol.renameTo(command.namespace).andThen(state -> answer.with(state));
    }
  }
}
