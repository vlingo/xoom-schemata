package io.vlingo.schemata.model.sourcing;

import io.vlingo.actors.testkit.TestUntil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chandrabhan Kumhar
 * Used to set result in every schemata
 */
public class Result {
    public List<Object> applied = new ArrayList<> ();
    public boolean defined;
    public boolean described;
    public boolean renamed;
    public boolean recategorised;
    public TestUntil until = TestUntil.happenings ( 1 );
}
