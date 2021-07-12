package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.model.SchemaVersion;
import io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import io.vlingo.xoom.schemata.query.CodeQueries;
import io.vlingo.xoom.schemata.query.view.CodeView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.infra.persistence.Fixtures.*;
import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class CodeProjectionTest extends ProjectionTest {
  private CodeQueries codeQueries;

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

    assertCompletes(codeQueries.codeFor(reference), (codeView) -> {
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

    assertCompletes(codeQueries.codeFor(reference), (codeView) -> {
      assertEquals(SchemaVersionVersion101.value, codeView.currentVersion());
      assertEquals(updatedSchemaSpecification.value, codeView.specification());
    });
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    codeQueries = StorageProvider.instance().codeQueries;
  }
}
