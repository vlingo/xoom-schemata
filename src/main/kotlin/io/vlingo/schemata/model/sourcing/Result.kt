package io.vlingo.schemata.model.sourcing

import io.vlingo.actors.testkit.TestUntil
import java.util.*

class Result {
    var applied: MutableList<String> = ArrayList()
    var tested1: Boolean = false
    var tested2: Boolean = false
    var until = TestUntil.happenings(1)
}
