// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

class SchemaVersionQueryExecutor {
  private final AtomicBoolean busy;
  private final Consumer<SchemaVersionQueryExecutor> queryRunner;
  private final AtomicReference<String> value;

  static SchemaVersionQueryExecutor initial(final Consumer<SchemaVersionQueryExecutor> queryRunner) {
    final SchemaVersionQueryExecutor queryExecutor = new SchemaVersionQueryExecutor(queryRunner);
    queryExecutor.execute();
    return queryExecutor;
  }

  boolean isBusy() {
    return busy.get();
  }

  void execute() {
    busy.set(true);
    queryRunner.accept(this);
  }

  boolean isResolved() {
    return !value().isEmpty();
  }

  SchemaVersionQueryExecutor resolveWith(final String value) {
    return new SchemaVersionQueryExecutor(queryRunner, value);
  }

  String value() {
    return value.get();
  }

  SchemaVersionQueryExecutor unresolved() {
    final SchemaVersionQueryExecutor answer = new SchemaVersionQueryExecutor(queryRunner);

    return answer;
  }

  private SchemaVersionQueryExecutor(final Consumer<SchemaVersionQueryExecutor> queryRunner) {
    this.queryRunner = queryRunner;
    this.value = new AtomicReference<>("");
    this.busy = new AtomicBoolean(false);
  }

  private SchemaVersionQueryExecutor(final Consumer<SchemaVersionQueryExecutor> queryRunner, final String value) {
    this.queryRunner = queryRunner;
    this.value = new AtomicReference<>(value);
    this.busy = new AtomicBoolean(false);
  }

  @Override
  public String toString() {
    return "SchemaVersionQueryExecutor [busy=" + busy + ", value=" + value + "]";
  }
}
