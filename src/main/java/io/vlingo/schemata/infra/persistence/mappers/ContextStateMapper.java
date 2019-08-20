// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence.mappers;

import io.vlingo.schemata.model.ContextState;
import io.vlingo.schemata.model.Id.ContextId;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContextStateMapper implements RowMapper<ContextState> {
    @Override
    public ContextState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return ContextState.from(
                rs.getLong("id"),
                ContextId.existing(
                        rs.getString("contextId") + ":" +
                        rs.getString("unitId") + ":" +
                        rs.getString("organizationId")),
                rs.getString("namespace"),
                rs.getString("description"));
    }
}
