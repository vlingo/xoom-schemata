// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.lattice.model.Command;
import io.vlingo.lattice.router.CommandDispatcher;
import io.vlingo.lattice.router.CommandRouter;
import io.vlingo.lattice.router.CommandRouter.Type;
import io.vlingo.lattice.router.RoutableCommand;
import io.vlingo.schemata.codegen.TypeDefinitionMiddleware;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.SchemaVersion;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersionEntity;
import io.vlingo.schemata.model.SchemaVersionState;
import io.vlingo.schemata.model.SpecificationDiff;
import io.vlingo.schemata.resource.data.SchemaVersionData;

class SchemaVersionCommands {
  private final CommandRouter router;
  private final Stage stage;

  SchemaVersionCommands(final Stage stage, final int routees) {
    this.stage = stage;
    this.router = CommandRouter.of(stage, Type.LoadBalancing, routees);
  }

  RoutableCommand<SchemaVersion,Command,SchemaVersionState> describeAs(
          final SchemaVersionId schemaVersionId,
          final String description) {

    final DescribeAs describeAs = new DescribeAs(description);

    RoutableCommand<SchemaVersion,Command,SchemaVersionState> command =
            RoutableCommand
              .speaks(SchemaVersion.class)
              .to(SchemaVersionEntity.class)
              .at(schemaVersionId.value)
              .createsWith(Definition.parameters(schemaVersionId))
              .named(SchemaVersion.nameFrom(schemaVersionId))
              .delivers(describeAs)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(describeAs);

    router.route(command);

    return command;
  }

  RoutableCommand<SchemaVersion,Command,SchemaVersionState> publish(
          final SchemaVersionId schemaVersionId) {

    final Publish publish = new Publish();

    RoutableCommand<SchemaVersion,Command,SchemaVersionState> command =
            RoutableCommand
              .speaks(SchemaVersion.class)
              .to(SchemaVersionEntity.class)
              .at(schemaVersionId.value)
              .createsWith(Definition.parameters(schemaVersionId))
              .named(SchemaVersion.nameFrom(schemaVersionId))
              .delivers(publish)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(publish);

    router.route(command);

    return command;
  }

  RoutableCommand<SchemaVersion,Command,SchemaVersionState> deprecate(
          final SchemaVersionId schemaVersionId) {

    final Deprecate deprecate = new Deprecate();

    RoutableCommand<SchemaVersion,Command,SchemaVersionState> command =
            RoutableCommand
              .speaks(SchemaVersion.class)
              .to(SchemaVersionEntity.class)
              .at(schemaVersionId.value)
              .createsWith(Definition.parameters(schemaVersionId))
              .named(SchemaVersion.nameFrom(schemaVersionId))
              .delivers(deprecate)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(deprecate);

    router.route(command);

    return command;
  }

  RoutableCommand<SchemaVersion,Command,SchemaVersionState> remove(
          final SchemaVersionId schemaVersionId) {

    final Remove remove = new Remove();

    RoutableCommand<SchemaVersion,Command,SchemaVersionState> command =
            RoutableCommand
              .speaks(SchemaVersion.class)
              .to(SchemaVersionEntity.class)
              .at(schemaVersionId.value)
              .createsWith(Definition.parameters(schemaVersionId))
              .named(SchemaVersion.nameFrom(schemaVersionId))
              .delivers(remove)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(remove);

    router.route(command);

    return command;
  }

  RoutableCommand<SchemaVersion,Command,SchemaVersionState> specifyWith(
          final SchemaVersionId schemaVersionId,
          final Specification specification) {

    final SpecifyWith specifyWith = new SpecifyWith(specification);

    RoutableCommand<SchemaVersion,Command,SchemaVersionState> command =
            RoutableCommand
              .speaks(SchemaVersion.class)
              .to(SchemaVersionEntity.class)
              .at(schemaVersionId.value)
              .createsWith(Definition.parameters(schemaVersionId))
              .named(SchemaVersion.nameFrom(schemaVersionId))
              .delivers(specifyWith)
              .answers(Completes.using(stage.scheduler()))
              .handledBy(specifyWith);

    router.route(command);

    return command;
  }

  RoutableCommand<SchemaVersion, Command, Outcome<SchemataBusinessException, SpecificationDiff>> diffAgainst(final SchemaVersionId schemaVersionId,
                                                                         final SchemaVersionData other) {

    final DiffAgainst diffAgainst = new DiffAgainst(other, TypeDefinitionMiddleware.middlewareFor(stage));

    RoutableCommand<SchemaVersion, Command, Outcome<SchemataBusinessException, SpecificationDiff>> command =
      RoutableCommand
        .speaks(SchemaVersion.class)
        .to(SchemaVersionEntity.class)
        .at(schemaVersionId.value)
        .createsWith(Definition.parameters(schemaVersionId))
        .named(SchemaVersion.nameFrom(schemaVersionId))
        .delivers(diffAgainst)
        .answers(Completes.using(stage.scheduler()))
        .handledBy(diffAgainst);

    router.route(command);

    return command;
  }

  private static class DescribeAs extends Command implements CommandDispatcher<SchemaVersion,DescribeAs,Completes<SchemaVersionState>> {
    private final String description;

    DescribeAs(final String description) {
      this.description = description;
    }

    @Override
    public void accept(final SchemaVersion protocol, final DescribeAs command, final Completes<SchemaVersionState> answer) {
      protocol.describeAs(command.description).andThen(state -> answer.with(state));
    }
  }

  private static class Publish extends Command implements CommandDispatcher<SchemaVersion,Publish,Completes<SchemaVersionState>> {
    Publish() { }

    @Override
    public void accept(final SchemaVersion protocol, final Publish command, final Completes<SchemaVersionState> answer) {
      protocol.publish().andThen(state -> answer.with(state));
    }
  }

  private static class Deprecate extends Command implements CommandDispatcher<SchemaVersion,Deprecate,Completes<SchemaVersionState>> {
    Deprecate() { }

    @Override
    public void accept(final SchemaVersion protocol, final Deprecate command,
                       final Completes<SchemaVersionState> answer) {
      protocol.deprecate().andThen(state -> answer.with(state));
    }
  }

  private static class Remove extends Command implements CommandDispatcher<SchemaVersion,Remove,Completes<SchemaVersionState>> {
    Remove() { }

    @Override
    public void accept(final SchemaVersion protocol, final Remove command, final Completes<SchemaVersionState> answer) {
      protocol.remove().andThen(state -> answer.with(state));
    }
  }

  private static class SpecifyWith extends Command implements CommandDispatcher<SchemaVersion,SpecifyWith,Completes<SchemaVersionState>> {
    private final Specification specification;

    SpecifyWith(final Specification specification) {
      this.specification = specification;
    }

    @Override
    public void accept(final SchemaVersion protocol, final SpecifyWith command, final Completes<SchemaVersionState> answer) {
      protocol.specifyWith(command.specification).andThen(state -> answer.with(state));
    }
  }

  private static class DiffAgainst extends Command
    implements CommandDispatcher<SchemaVersion, DiffAgainst, Completes<Outcome<SchemataBusinessException, SpecificationDiff>>> {
    private final SchemaVersionData other;
    private final TypeDefinitionMiddleware typeDefinitionMiddleware;

    DiffAgainst(final SchemaVersionData other, TypeDefinitionMiddleware typeDefinitionMiddleware) {
      this.other = other;
      this.typeDefinitionMiddleware = typeDefinitionMiddleware;
    }

    @Override
    public void accept(final SchemaVersion protocol, final DiffAgainst command,
                       final Completes<Outcome<SchemataBusinessException, SpecificationDiff>> answer) {
      protocol.diff(typeDefinitionMiddleware, command.other).andThen(diff -> answer.with(diff));
    }
  }
}
