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

import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.model.Id.ContextId;

public class ContextStateMapper implements RowMapper<ContextState> {
    @Override
    public ContextState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ContextState.from(
                rs.getLong("id"),
                rs.getLong("dataVersion"),
                ContextId.existing(
                        rs.getString("organizationId"),
                        rs.getString("unitId"),
                        rs.getString("contextId")),
                rs.getString("namespace"),
                rs.getString("description"));
    }
}
