// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;

public class SchemaVersionStateMapper implements RowMapper<SchemaVersionState> {
    @Override
    public SchemaVersionState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return SchemaVersionState.from(
                rs.getLong("id"),
                rs.getLong("dataVersion"),
                SchemaVersionId.existing(
                        rs.getString("organizationId"),
                        rs.getString("unitId"),
                        rs.getString("contextId"),
                        rs.getString("schemaId"),
                        rs.getString("schemaVersionId")),
                Specification.of(rs.getString("specification")),
                rs.getString("description"),
                Status.valueOf(rs.getString("status")),
                Version.of(rs.getString("previousVersion")),
                Version.of(rs.getString("currentVersion")));
    }
}
