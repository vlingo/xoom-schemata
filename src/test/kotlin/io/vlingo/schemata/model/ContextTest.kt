package io.vlingo.schemata.model

import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.actors.testkit.TestUntil
import io.vlingo.schemata.model.sourcing.Result
import org.junit.Before
import org.junit.Test

/**
 * @author Chandrabhan Kumhar
 * Test for Context
 */
class ContextTest {
    private var result: Result? = null
    private var context: Context? = null
    private var world: World? = null

    /**
     * Used to initialize objects
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        world = World.startWithDefaults("test-es")
        result = Result()
        context = world!!.actorFor(Definition.has(Context::class.java, Definition.NoParameters), Context::class.java)

    }

    /**
     * Used to test the nameSpace of Context
     */
    @Test
    @Throws(Exception::class)
    fun changeNamespaceTo() {
        result!!.until.completes()
        result!!.until = TestUntil.happenings(1)
        context!!.changeNamespaceTo("namespace")
    }

    /**
     * Used to test the description of Context
     */
    @Test
    @Throws(Exception::class)
    fun describeAs() {
        context!!.describeAs("desc")
    }

}