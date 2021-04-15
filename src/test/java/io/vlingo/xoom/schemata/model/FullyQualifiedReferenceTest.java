package io.vlingo.xoom.schemata.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class FullyQualifiedReferenceTest {

    @Test
    public void schemaReferenceCanBeProducedFromReferenceString() {
        String refStr = "org:unit:context:schema";
        FullyQualifiedReference ref = FullyQualifiedReference.from(refStr);

        assertEquals("org", ref.organization);
        assertEquals("unit", ref.unit);
        assertEquals("context", ref.context);
        assertEquals("schema", ref.schema);

        assertFalse(ref.isSchemaVersionReference());
    }

    @Test
    public void schemaVersionReferenceCanBeProducedFromReferenceString() {
        String refStr = "org:unit:context:schema:0.1.1";
        FullyQualifiedReference ref = FullyQualifiedReference.from(refStr);

        assertEquals("org", ref.organization);
        assertEquals("unit", ref.unit);
        assertEquals("context", ref.context);
        assertEquals("schema", ref.schema);
        assertEquals("0.1.1", ref.schemaVersion);

        assertTrue(ref.isSchemaVersionReference());
    }

    @Test(expected = IllegalArgumentException.class)
    public void incompleteReferencesCannotBeCreated() {
        String refStr = "org:unit:context";
        FullyQualifiedReference.from(refStr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void referencesWithEmptyPartsCannotBeCreated() {
        String refStr = "org:unit::schema";
        FullyQualifiedReference.from(refStr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void referencesWithTooManyPartsCannotBeCreated() {
        String refStr = "org:unit:context:schema:0.0.0:invalid";
        FullyQualifiedReference.from(refStr);
    }

}