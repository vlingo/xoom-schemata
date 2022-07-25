// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.schemata.model.SchemaVersionState;

import java.util.ArrayList;
import java.util.List;

public class SchemaVersionData {
  public final String organizationId;
  public final String unitId;
  public final String contextId;
  public final String schemaId;
  public final String schemaVersionId;
  public final String description;
  public final String specification;
  public final String status;
  public final String previousVersion;
  public final String currentVersion;

  public static SchemaVersionData from(final SchemaVersionState state) {
    return new SchemaVersionData(
            state.schemaVersionId.organizationId().value,
            state.schemaVersionId.unitId().value,
            state.schemaVersionId.contextId().value,
            state.schemaVersionId.schemaId.value,
            state.schemaVersionId.value,
            state.specification.value,
            state.description,
            state.status.value,
            state.previousVersion.value,
            state.currentVersion.value);
  }

  public static List<SchemaVersionData> from(final List<SchemaVersionState> states) {
    final List<SchemaVersionData> data = new ArrayList<>(states.size());

    for (final SchemaVersionState state : states) {
      data.add(SchemaVersionData.from(state));
    }

    return data;
  }

  @JsonCreator
  public static SchemaVersionData from(
          @JsonProperty("organizationId") final String organizationId,
          @JsonProperty("unitId") final String unitId,
          @JsonProperty("contextId") final String contextId,
          @JsonProperty("schemaId") final String schemaId,
          @JsonProperty("schemaVersionId") final String schemaVersionId,
          @JsonProperty("specification") final String specification,
          @JsonProperty("description") final String description,
          @JsonProperty("status") final String status,
          @JsonProperty("previousVersion") final String previousVersion,
          @JsonProperty("nextVersion") final String nextVersion) {
  return new SchemaVersionData(
          organizationId,
          unitId,
          contextId,
          schemaId,
          schemaVersionId,
          specification,
          description,
          status,
          previousVersion,
          nextVersion);
  }

  public static SchemaVersionData just(
          final String specification,
          final String description,
          final String status,
          final String previousVersion,
          final String nextVersion) {
  return new SchemaVersionData(
          "", "", "", "", "",
          specification,
          description,
          status,
          previousVersion,
          nextVersion);
  }

  public static SchemaVersionData none() {
    return just("", "", "", "", "");
  }

  public static boolean hasSpecification(final String specification) {
    return specification != null && !specification.isEmpty();
  }

  public boolean isNone() {
    return organizationId.isEmpty() && unitId.isEmpty() && contextId.isEmpty() && schemaId.isEmpty() && schemaVersionId.isEmpty();
  }

  public static boolean validVersions(final String previousVersion, final String currentVersion) {
    try {
      final SemanticVersion previousSemantic = SemanticVersion.from(previousVersion);
      final SemanticVersion currentSemantic = SemanticVersion.from(currentVersion);

      if(!currentSemantic.isNonZero()) {
        return false;
      }

      if(!previousSemantic.isNonZero()) {
        return true; // initial version
      }

      if (currentSemantic.isNonZero()
        && currentSemantic.isGreaterThan(previousSemantic)
        && (
          (currentSemantic.major == previousSemantic.major + 1)
          || (currentSemantic.minor == previousSemantic.minor + 1)
        )
      ) {
        return true;
      }

      return (
          currentSemantic.isNonZero() &&
          currentSemantic.isCompatibleWith(previousSemantic) &&
          currentSemantic.isGreaterThan(previousSemantic));

    } catch (Exception e) {
      // fall through
    }

    return false;
  }

  public boolean hasSpecification() {
    return hasSpecification(specification);
  }

  public boolean validVersions() {
    return validVersions(previousVersion, currentVersion);
  }

  @Override
  public String toString() {
    return "SchemaVersionData [organizationId=" + organizationId + ", unitId=" + unitId + ", contextId=" + contextId
            + ", schemaId=" + schemaId + ", schemaVersionId=" + schemaVersionId + ", specification=" + specification
            + ", description=" + description + ", status=" + status + ", previousVersion=" + previousVersion
            + ", currentVersion=" + currentVersion + "]";
  }

  private SchemaVersionData(
          final String organizationId,
          final String unitId,
          final String contextId,
          final String schemaId,
          final String schemaVersionId,
          final String specification,
          final String description,
          final String status,
          final String previousVersion,
          final String nextVersion) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
    this.schemaId = schemaId;
    this.schemaVersionId = schemaVersionId;
    this.specification = specification;
    this.description = description;
    this.status = status;
    this.previousVersion = previousVersion;
    this.currentVersion = nextVersion;
  }
}
