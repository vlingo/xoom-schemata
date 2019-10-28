// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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

import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.SchemaState;
import io.vlingo.schemata.model.Scope;

public class SchemaStateMapper implements RowMapper<SchemaState> {
    @Override
    public SchemaState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return SchemaState.from(
                rs.getLong("id"),
                rs.getLong("dataVersion"),
                SchemaId.existing(
                        rs.getString("organizationId"),
                        rs.getString("unitId"),
                        rs.getString("contextId"),
                        rs.getString("schemaId")),
                Category.valueOf(rs.getString("category")),
                Scope.valueOf(rs.getString("scope")),
                rs.getString("name"),
                rs.getString("description"));
    }
}
