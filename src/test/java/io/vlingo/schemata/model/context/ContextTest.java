package io.vlingo.schemata.model.context;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.schemata.model.events.contextevent.ContextDefined;
import io.vlingo.schemata.model.events.contextevent.ContextDescribed;
import io.vlingo.schemata.model.events.contextevent.ContextRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContextTest {
    private ContextEntity entity;
    private Result result;
    private World world;


    @Before
    public void setUp() throws Exception {
        world = World.startWithDefaults ( "context-test" );
        result = new Result ();
        entity = world.actorFor ( Definition.has ( Context.class, Definition.parameters ( result ) ), ContextEntity.class );
    }

    @Test
    public void testApplyDefinedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( ContextDefined.class, result.applied.get ( 0 ).getClass () );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.recategorised );
    }

    @Test
    public void testApplyDescribedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.recategorised );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( ContextDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "desc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( ContextDescribed.class, result.applied.get ( 1 ).getClass () );


    }

    @Test
    public void applyNamespaceChangedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.recategorised );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( ContextDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "description" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( ContextDescribed.class, result.applied.get ( 1 ).getClass () );
        result.until = TestUntil.happenings ( 2 );
        entity.changeNamespaceTo ( "newNamespace" );
        result.until.completes ();
        Assert.assertEquals ( 3, result.applied.size () );
        Assert.assertEquals ( ContextRenamed.class, result.applied.get ( 2 ).getClass () );
    }

}