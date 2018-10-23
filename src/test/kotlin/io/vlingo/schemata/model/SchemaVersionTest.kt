package io.vlingo.schemata.model

import org.junit.Before
import org.junit.Test

/**
 * @author Chandrabhan Kumhar
 * Test for SchemaVersion
 */
class SchemaVersionTest {

    var schemaVersion: SchemaVersion? = null

    /**
     * Used to initialize Objects
     */
    @Before
    fun setUp() {
        schemaVersion = SchemaVersion()
    }

    /**
     * Used to test set state of SchemaVersion
     */
    @Test
    fun getState() {
        schemaVersion!!.state
    }

    /**
     * Used to test get state of SchemaVersion
     */
    @Test
    fun setState() {

    }

}