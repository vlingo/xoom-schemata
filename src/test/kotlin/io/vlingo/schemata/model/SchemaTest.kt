package io.vlingo.schemata.model

import org.junit.Before
import org.junit.Test

/**
 * @author Chandrabhan Kumhar
 * Test for SchemaTest
 */
class SchemaTest {
    var schema: Schema? = null

    /**
     * Used to initialize Objects
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        schema = Schema.defineWith(Id.OrganizationId.unique(), Id.ContextId.unique(), Category.Data, "name", "desc")
    }

    /**
     * Used to test description of Schema
     */
    @Test
    @Throws(Exception::class)
    fun describeAs() {
        schema!!.describeAs("desc")
    }

    /**
     * Used to test reCategorized of Schema
     */
    @Test
    @Throws(Exception::class)
    fun recategorizedAs() {
        schema!!.recategorizedAs(Category.Commands)
    }

    /**
     * Used to test rename of Schema
     */
    @Test
    @Throws(Exception::class)
    fun renameTo() {
        schema!!.renameTo("reName")
    }

}