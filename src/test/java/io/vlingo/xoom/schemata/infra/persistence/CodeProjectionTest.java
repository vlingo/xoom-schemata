package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.model.SchemaVersion;
import io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import io.vlingo.xoom.schemata.query.view.CodeView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.infra.persistence.Fixtures.*;
import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class CodeProjectionTest extends ProjectionTest {

  @Test
  public void itProjectsCodeViewFromSchemaVersionDefinedEvents() {

    onceProjected(CodeView.class, () -> givenAnySchema()
            .andThenTo(schema -> SchemaVersion.with(
                    stage,
                    schema.schemaId,
                    SchemaVersionSpecification,
                    SchemaVersionDescription,
                    SchemaVersionVersion000,
                    SchemaVersionVersion100
            ))
    );

    final Path reference = Path.with(OrgName, UnitName, ContextNamespace, SchemaName, SchemaVersionVersion100.value);

    assertCompletes(codeView(reference), (codeView) -> {
      assertEquals(SchemaVersionVersion100.value, codeView.currentVersion());
      assertEquals(SchemaVersionSpecification.value, codeView.specification());
    });
  }

  @Test
  public void itUpdatesCodeViewFromSchemaVersionDefinedEvents() {

    final Specification updatedSchemaSpecification = Specification.of("event SchemaDefined { type eventType }");

    onceProjected(CodeView.class, () -> givenAnySchemaVersion()
            .andThenTo(schemaVersion -> SchemaVersion.with(
                    stage,
                    schemaVersion.schemaVersionId.schemaId,
                    updatedSchemaSpecification,
                    "Updated description",
                    schemaVersion.currentVersion,
                    SchemaVersionVersion101
            ))
    );

    final Path reference = Path.with(OrgName, UnitName, ContextNamespace, SchemaName, SchemaVersionVersion101.value);

    assertCompletes(codeView(reference), (codeView) -> {
      assertEquals(SchemaVersionVersion101.value, codeView.currentVersion());
      assertEquals(updatedSchemaSpecification.value, codeView.specification());
    });
  }

  private Completes<CodeView> codeView(Path reference) {
    return StorageProvider.instance().codeQueries.codeFor(reference);
  }
}
