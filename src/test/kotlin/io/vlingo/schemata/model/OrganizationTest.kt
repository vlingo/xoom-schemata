package io.vlingo.schemata.model

import io.vlingo.actors.World
import org.junit.Before
import org.junit.Test

/**
 * @author Chandrabhan Kumhar
 * Test for Organization
 */
class OrganizationTest {
    private var organization: Organization? = null;

    /**
     * Used to initialize Objects
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        World.startWithDefaults("test-es")
        organization = Organization.defineWith(Id.OrganizationId.unique(), "organization", "desc")

    }

    /**
     * Used to test description of Organization
     */
    @Test
    @Throws(Exception::class)
    fun describeAs() {
        organization!!.describeAs("desc")
    }

    /**
     * Used to test rename of Organization
     */
    @Test
    @Throws(Exception::class)
    fun renameTo() {
        organization!!.renameTo("newDesc")
    }

}