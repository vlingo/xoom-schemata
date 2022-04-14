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
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.model.Schema;
import io.vlingo.xoom.schemata.model.SchemaEntity;
import io.vlingo.xoom.schemata.model.SchemaState;
import io.vlingo.xoom.schemata.model.Scope;

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
              .createsWith(Definition.parameters(schemaId))
              .named(Schema.nameFrom(schemaId))
              .delivers(categorizedAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(categorizedAs);

    router.route(command);

    return command;
  }

  RoutableCommand<Schema,Command,SchemaState> scopeAs(
          final SchemaId schemaId,
          final Scope scope) {

    final ScopeAs scopeAs = new ScopeAs(scope);

    RoutableCommand<Schema,Command,SchemaState> command =
            RoutableCommand
              .speaks(Schema.class)
              .to(SchemaEntity.class)
              .at(schemaId.value)
              .createsWith(Definition.parameters(schemaId))
              .named(Schema.nameFrom(schemaId))
              .delivers(scopeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(scopeAs);

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
              .createsWith(Definition.parameters(schemaId))
              .named(Schema.nameFrom(schemaId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  public RoutableCommand<Schema, Command, SchemaState> redefineWith(
          final SchemaId schemaId,
          final Category category,
          final Scope scope,
          final String name,
          final String description) {

    final RedefineWith redefineWith = new RedefineWith(category, scope, name, description);

    RoutableCommand<Schema,Command,SchemaState> command =
            RoutableCommand
              .speaks(Schema.class)
              .to(SchemaEntity.class)
              .at(schemaId.value)
              .createsWith(Definition.parameters(schemaId))
              .named(Schema.nameFrom(schemaId))
              .delivers(redefineWith)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(redefineWith);

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
              .createsWith(Definition.parameters(schemaId))
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

  private static class ScopeAs extends Command implements CommandDispatcher<Schema,ScopeAs,Completes<SchemaState>> {
    private final Scope scope;

    ScopeAs(final Scope scope) {
      this.scope = scope;
    }

    @Override
    public void accept(final Schema protocol, final ScopeAs command, final Completes<SchemaState> answer) {
      protocol.scopeAs(command.scope).andThen(state -> answer.with(state));
    }
  }

  private static class RedefineWith extends Command implements CommandDispatcher<Schema,RedefineWith,Completes<SchemaState>> {
    private final Category category;
    private final Scope scope;
    private final String name;
    private final String description;

    RedefineWith(final Category category, final Scope scope, final String name, final String description) {
      this.category = category;
      this.scope = scope;
      this.name = name;
      this.description = description;
    }

    @Override
    public void accept(final Schema protocol, final RedefineWith command, final Completes<SchemaState> answer) {
      protocol.redefineWith(command.category, command.scope, command.name, command.description).andThen(state -> answer.with(state));
    }
  }

  private static class RenameTo extends Command implements CommandDispatcher<Schema,RenameTo,Completes<SchemaState>> {
    private final String name;

    RenameTo(final String name) {
      this.name = name;
    }

    @Override
    public void accept(final Schema protocol, final RenameTo command, final Completes<SchemaState> answer) {
      protocol.renameTo(command.name).andThen(state -> answer.with(state));
    }
  }
}
