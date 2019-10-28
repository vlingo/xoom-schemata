// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

public class PostgresSchemataObjectStore extends SchemataObjectStore {
  public PostgresSchemataObjectStore() throws Exception {
  }

  @Override
  protected void createOrganizationStateTable() {
      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_ORGANIZATIONS (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +
              "organizationId VARCHAR (50) NOT NULL, " +
              "name VARCHAR(128) NOT NULL, " +
              "description VARCHAR(8000) " +

              ")");

      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS ORG_ALL_INDEX ON TBL_ORGANIZATIONS (organizationId)");

      /*
      Dropping the constraint and recreating it afterwards is not an optimal solution once we have actual data.

      TODO: Refactor to find out if constraint already exists or switch to a schema migration tool like flyway or liquibase
       */
      jdbi.handle().execute("ALTER TABLE TBL_ORGANIZATIONS DROP CONSTRAINT IF EXISTS ORGANIZATION_UNIQUE");
      jdbi.handle().execute("ALTER TABLE TBL_ORGANIZATIONS ADD CONSTRAINT ORGANIZATION_UNIQUE UNIQUE (name)");
  }

  @Override
  protected void createUnitStateTable() {
      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_UNITS (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +
              "unitId VARCHAR(50) NOT NULL, " +
              "organizationId VARCHAR (50) NOT NULL, " +
              "name VARCHAR(128) NOT NULL, " +
              "description VARCHAR(8000) " +

              ")");

      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS UNIT_PARENT_INDEX ON TBL_UNITS (organizationId)");
      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS UNIT_ALL_INDEX ON TBL_UNITS (organizationId, unitId)");

      jdbi.handle().execute("ALTER TABLE TBL_UNITS DROP CONSTRAINT IF EXISTS UNIT_ALL_UNIQUE");
      jdbi.handle().execute("ALTER TABLE TBL_UNITS ADD CONSTRAINT UNIT_ALL_UNIQUE UNIQUE (organizationId, name)");
  }

  @Override
  protected void createContextStateTable() {
      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_CONTEXTS (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +
              "contextId VARCHAR(50) NOT NULL, " +
              "unitId VARCHAR(50) NOT NULL, " +
              "organizationId VARCHAR (50) NOT NULL, " +
              "namespace VARCHAR(256) NOT NULL, " +
              "description VARCHAR(8000) " +

              ")");

      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS CONTEXT_PARENT_INDEX ON TBL_CONTEXTS (organizationId, unitId)");
      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS CONTEXT_ALL_INDEX ON TBL_CONTEXTS (organizationId, unitId, contextId)");

      jdbi.handle().execute("ALTER TABLE TBL_CONTEXTS DROP CONSTRAINT IF EXISTS CONTEXT_ALL_UNIQUE");
      jdbi.handle().execute("ALTER TABLE TBL_CONTEXTS ADD CONSTRAINT CONTEXT_ALL_UNIQUE UNIQUE (organizationId, unitId, namespace)");
  }

  @Override
  protected void createSchemaStateTable() {
      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_SCHEMAS (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +
              "schemaId VARCHAR(50) NOT NULL, " +
              "contextId VARCHAR(50) NOT NULL, " +
              "unitId VARCHAR(50) NOT NULL, " +
              "organizationId VARCHAR (50) NOT NULL, " +
              "category VARCHAR(25) NOT NULL, " +
              "scope VARCHAR(25) NOT NULL, " +
              "name VARCHAR(128) NOT NULL, " +
              "description VARCHAR(8000) " +

              ")");

      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMA_PARENT_INDEX ON TBL_SCHEMAS (organizationId, unitId, contextId)");
      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMA_ALL_INDEX ON TBL_SCHEMAS (organizationId, unitId, contextId, schemaId)");

      jdbi.handle().execute("ALTER TABLE TBL_SCHEMAS DROP CONSTRAINT IF EXISTS SCHEMA_ALL_UNIQUE");
      jdbi.handle().execute("ALTER TABLE TBL_SCHEMAS ADD CONSTRAINT SCHEMA_ALL_UNIQUE UNIQUE (organizationId, unitId, contextId, name)");

  }

  @Override
  protected void createSchemaVersionStateTable() {
      final int specificationWidth;

      switch (jdbi.databaseType()) {
      case Postgres:
        specificationWidth = 65536;
        break;
      case HSQLDB:
        specificationWidth = 8000;
        break;
      default:
        specificationWidth = 4000;
        break;
      }

      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_SCHEMAVERSIONS (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +
              "schemaVersionId VARCHAR(50) NOT NULL, " +
              "schemaId VARCHAR(50) NOT NULL, " +
              "contextId VARCHAR(50) NOT NULL, " +
              "unitId VARCHAR(50) NOT NULL, " +
              "organizationId VARCHAR (50) NOT NULL, " +
              "specification VARCHAR(" + specificationWidth + ") NOT NULL, " +
              "description VARCHAR(8000), " +
              "status VARCHAR(16) NOT NULL, " +
              "previousVersion VARCHAR(20) NOT NULL, " +
              "currentVersion VARCHAR(20) NOT NULL " +

              ")");

      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMAVERSION_PARENT_INDEX ON TBL_SCHEMAVERSIONS (organizationId, unitId, contextId, schemaId)");
      jdbi.handle().execute("CREATE UNIQUE INDEX IF NOT EXISTS SCHEMAVERSION_ALL_INDEX ON TBL_SCHEMAVERSIONS (organizationId, unitId, contextId, schemaId, schemaVersionId)");

      jdbi.handle().execute("ALTER TABLE TBL_SCHEMAVERSIONS DROP CONSTRAINT IF EXISTS SCHEMAVERSIION_ALL_UNIQUE");
      jdbi.handle().execute("ALTER TABLE TBL_SCHEMAVERSIONS ADD CONSTRAINT SCHEMAVERSIION_ALL_UNIQUE UNIQUE (organizationId, unitId, contextId, schemaId, currentVersion)");
  }

  @Override
  protected void createDependencyStateTable() {
      jdbi.handle().execute(
              "CREATE TABLE IF NOT EXISTS TBL_DEPENDENCIES (" +
              "id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1) PRIMARY KEY, " +
              "dataVersion BIGINT, " +

              "sourceOrganizationId VARCHAR (50) NOT NULL, " +
              "sourceUnitId VARCHAR(50) NOT NULL, " +
              "sourceContextId VARCHAR(50) NOT NULL, " +
              "sourceSchemaId VARCHAR(50) NOT NULL, " +
              "sourceSchemaVersionId VARCHAR(50) NOT NULL, " +

              "dependentOrganizationId VARCHAR (50) NOT NULL, " +
              "dependentUnitId VARCHAR(50) NOT NULL, " +
              "dependentContextId VARCHAR(50) NOT NULL " +

              ")");
  }
}
