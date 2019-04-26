// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry.Info;
import io.vlingo.schemata.model.ContextEntity;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRecategorized;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Events.SchemaVersionAssignedVersion;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.model.SchemaEntity;
import io.vlingo.schemata.model.SchemaVersionEntity;
import io.vlingo.schemata.model.UnitEntity;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.store.journal.Journal;

public class EntryAdapters {
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void register(final SourcedTypeRegistry registry, final Journal<?> journal) {

    registry.register(new Info(journal, OrganizationEntity.class, OrganizationEntity.class.getSimpleName()));
    registry.register(new Info(journal, UnitEntity.class, UnitEntity.class.getSimpleName()));
    registry.register(new Info(journal, ContextEntity.class, ContextEntity.class.getSimpleName()));
    registry.register(new Info(journal, SchemaEntity.class, SchemaEntity.class.getSimpleName()));
    registry.register(new Info(journal, SchemaVersionEntity.class, SchemaVersionEntity.class.getSimpleName()));

    registry.info(OrganizationEntity.class)
      .registerEntryAdapter(OrganizationDefined.class, new OrganizationDefinedAdapter())
      .registerEntryAdapter(OrganizationDescribed.class, new OrganizationDescribedAdapter())
      .registerEntryAdapter(OrganizationRenamed.class, new OrganizationRenamedAdapter());

    registry.info(UnitEntity.class)
      .registerEntryAdapter(UnitDefined.class, new UnitDefinedAdapter())
      .registerEntryAdapter(UnitDescribed.class, new UnitDescribedAdapter())
      .registerEntryAdapter(UnitRenamed.class, new UnitRenamedAdapter());

    registry.info(ContextEntity.class)
      .registerEntryAdapter(ContextDefined.class, new ContextDefinedAdapter())
      .registerEntryAdapter(ContextRenamed.class, new ContextRenamedAdapter())
      .registerEntryAdapter(ContextDescribed.class, new ContextDescribedAdapter());

    registry.info(SchemaEntity.class)
      .registerEntryAdapter(SchemaDefined.class, new SchemaDefinedAdapter())
      .registerEntryAdapter(SchemaDescribed.class, new SchemaDescribedAdapter())
      .registerEntryAdapter(SchemaRecategorized.class, new SchemaRecategorizedAdapter())
      .registerEntryAdapter(SchemaRenamed.class, new SchemaRenamedAdapter());

    registry.info(SchemaVersionEntity.class)
      .registerEntryAdapter(SchemaVersionDefined.class, new SchemaVersionDefinedAdapter())
      .registerEntryAdapter(SchemaVersionAssignedVersion.class, new SchemaVersionAssignedVersionAdapter())
      .registerEntryAdapter(SchemaVersionDescribed.class, new SchemaVersionDescribedAdapter())
      .registerEntryAdapter(SchemaVersionPublished.class, new SchemaVersionPublishedAdapter())
      .registerEntryAdapter(SchemaVersionRemoved.class, new SchemaVersionRemovedAdapter())
      .registerEntryAdapter(SchemaVersionSpecified.class, new SchemaVersionSpecifiedAdapter());
  }

  //================================
  // Organization
  //================================

  public static final class OrganizationDefinedAdapter implements EntryAdapter<OrganizationDefined,TextEntry> {
    @Override
    public OrganizationDefined fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), OrganizationDefined.class);
    }

    @Override
    public TextEntry toEntry(final OrganizationDefined source) {
      return toEntry(source, source.organizationId);
    }

    @Override
    public TextEntry toEntry(final OrganizationDefined source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, OrganizationDefined.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class OrganizationDescribedAdapter implements EntryAdapter<OrganizationDescribed,TextEntry> {
    @Override
    public OrganizationDescribed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), OrganizationDescribed.class);
    }

    @Override
    public TextEntry toEntry(final OrganizationDescribed source) {
      return toEntry(source, source.organizationId);
    }

    @Override
    public TextEntry toEntry(final OrganizationDescribed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, OrganizationDescribed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class OrganizationRenamedAdapter implements EntryAdapter<OrganizationRenamed,TextEntry> {
    @Override
    public OrganizationRenamed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), OrganizationRenamed.class);
    }

    @Override
    public TextEntry toEntry(final OrganizationRenamed source) {
      return toEntry(source, source.organizationId);
    }

    @Override
    public TextEntry toEntry(final OrganizationRenamed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, OrganizationRenamed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  //================================
  // Unit
  //================================

  public static final class UnitDefinedAdapter implements EntryAdapter<UnitDefined,TextEntry> {
    @Override
    public UnitDefined fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), UnitDefined.class);
    }

    @Override
    public TextEntry toEntry(final UnitDefined source) {
      return toEntry(source, source.unitId);
    }

    @Override
    public TextEntry toEntry(final UnitDefined source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, UnitDefined.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class UnitDescribedAdapter implements EntryAdapter<UnitDescribed,TextEntry> {
    @Override
    public UnitDescribed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), UnitDescribed.class);
    }

    @Override
    public TextEntry toEntry(final UnitDescribed source) {
      return toEntry(source, source.unitId);
    }

    @Override
    public TextEntry toEntry(final UnitDescribed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, UnitDescribed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class UnitRenamedAdapter implements EntryAdapter<UnitRenamed,TextEntry> {
    @Override
    public UnitRenamed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), UnitRenamed.class);
    }

    @Override
    public TextEntry toEntry(final UnitRenamed source) {
      return toEntry(source, source.unitId);
    }

    @Override
    public TextEntry toEntry(final UnitRenamed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, UnitRenamed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  //================================
  // Context
  //================================

  public static final class ContextDefinedAdapter implements EntryAdapter<ContextDefined,TextEntry> {
    @Override
    public ContextDefined fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), ContextDefined.class);
    }

    @Override
    public TextEntry toEntry(final ContextDefined source) {
      return toEntry(source, source.contextId);
    }

    @Override
    public TextEntry toEntry(final ContextDefined source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, ContextDefined.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class ContextRenamedAdapter implements EntryAdapter<ContextRenamed,TextEntry> {
    @Override
    public ContextRenamed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), ContextRenamed.class);
    }

    @Override
    public TextEntry toEntry(final ContextRenamed source) {
      return toEntry(source, source.contextId);
    }

    @Override
    public TextEntry toEntry(final ContextRenamed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, ContextRenamed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class ContextDescribedAdapter implements EntryAdapter<ContextDescribed,TextEntry> {
    @Override
    public ContextDescribed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), ContextDescribed.class);
    }

    @Override
    public TextEntry toEntry(final ContextDescribed source) {
      return toEntry(source, source.contextId);
    }

    @Override
    public TextEntry toEntry(final ContextDescribed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, ContextDescribed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  //================================
  // Schema
  //================================

  public static final class SchemaDefinedAdapter implements EntryAdapter<SchemaDefined,TextEntry> {
    @Override
    public SchemaDefined fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaDefined.class);
    }

    @Override
    public TextEntry toEntry(final SchemaDefined source) {
      return toEntry(source, source.schemaId);
    }

    @Override
    public TextEntry toEntry(final SchemaDefined source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaDefined.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaDescribedAdapter implements EntryAdapter<SchemaDescribed,TextEntry> {
    @Override
    public SchemaDescribed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaDescribed.class);
    }

    @Override
    public TextEntry toEntry(final SchemaDescribed source) {
      return toEntry(source, source.schemaId);
    }

    @Override
    public TextEntry toEntry(final SchemaDescribed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaDescribed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaRecategorizedAdapter implements EntryAdapter<SchemaRecategorized,TextEntry> {
    @Override
    public SchemaRecategorized fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaRecategorized.class);
    }

    @Override
    public TextEntry toEntry(final SchemaRecategorized source) {
      return toEntry(source, source.schemaId);
    }

    @Override
    public TextEntry toEntry(final SchemaRecategorized source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaRecategorized.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaRenamedAdapter implements EntryAdapter<SchemaRenamed,TextEntry> {
    @Override
    public SchemaRenamed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaRenamed.class);
    }

    @Override
    public TextEntry toEntry(final SchemaRenamed source) {
      return toEntry(source, source.schemaId);
    }

    @Override
    public TextEntry toEntry(final SchemaRenamed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaRenamed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  //================================
  // SchemaVersion
  //================================

  public static final class SchemaVersionDefinedAdapter implements EntryAdapter<SchemaVersionDefined,TextEntry> {
    @Override
    public SchemaVersionDefined fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionDefined.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionDefined source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionDefined source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionDefined.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaVersionAssignedVersionAdapter implements EntryAdapter<SchemaVersionAssignedVersion,TextEntry> {
    @Override
    public SchemaVersionAssignedVersion fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionAssignedVersion.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionAssignedVersion source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionAssignedVersion source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionAssignedVersion.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaVersionDescribedAdapter implements EntryAdapter<SchemaVersionDescribed,TextEntry> {
    @Override
    public SchemaVersionDescribed fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionDescribed.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionDescribed source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionDescribed source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionDescribed.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaVersionPublishedAdapter implements EntryAdapter<SchemaVersionPublished,TextEntry> {
    @Override
    public SchemaVersionPublished fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionPublished.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionPublished source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionPublished source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionPublished.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaVersionRemovedAdapter implements EntryAdapter<SchemaVersionRemoved,TextEntry> {
    @Override
    public SchemaVersionRemoved fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionRemoved.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionRemoved source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionRemoved source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionRemoved.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  public static final class SchemaVersionSpecifiedAdapter implements EntryAdapter<SchemaVersionSpecified,TextEntry> {
    @Override
    public SchemaVersionSpecified fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), SchemaVersionSpecified.class);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionSpecified source) {
      return toEntry(source, source.schemaVersionId);
    }

    @Override
    public TextEntry toEntry(final SchemaVersionSpecified source, final String id) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, SchemaVersionSpecified.class, 1, serialization, Metadata.nullMetadata());
    }
  }
}
