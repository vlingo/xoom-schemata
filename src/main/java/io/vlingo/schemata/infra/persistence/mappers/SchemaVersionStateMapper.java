// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence.mappers;

import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.SchemaVersion.Specification;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.model.SchemaVersion.Version;
import io.vlingo.schemata.model.SchemaVersionState;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SchemaVersionStateMapper implements RowMapper<SchemaVersionState> {
    @Override
    public SchemaVersionState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return SchemaVersionState.from(
                rs.getLong("id"),
                SchemaVersionId.existing(
                        rs.getString("schemaVersionId") + ":" +
                        rs.getString("schemaId") + ":" +
                        rs.getString("contextId") + ":" +
                        rs.getString("unitId") + ":" +
                        rs.getString("organizationId")),
                rs.getString("description"),
                Specification.of(rs.getString("specification")),
                Status.valueOf(rs.getString("status")),
                Version.of(rs.getString("versionState")));
    }
}
