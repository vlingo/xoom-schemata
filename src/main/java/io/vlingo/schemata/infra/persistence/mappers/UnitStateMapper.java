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

import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.UnitState;

public class UnitStateMapper implements RowMapper<UnitState> {
    @Override
    public UnitState map(ResultSet rs, StatementContext ctx) throws SQLException {
        return UnitState.from(
                rs.getLong("id"),
                rs.getLong("dataVersion"),
                UnitId.existing(
                        rs.getString("organizationId"),
                        rs.getString("unitId")),
                rs.getString("name"),
                rs.getString("description"));
    }
}
