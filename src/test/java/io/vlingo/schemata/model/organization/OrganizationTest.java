package io.vlingo.schemata.model.organization;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.schemata.model.events.organizationevent.OrganizationDefined;
import io.vlingo.schemata.model.events.organizationevent.OrganizationDescribed;
import io.vlingo.schemata.model.events.organizationevent.OrganizationRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrganizationTest {
    private OrganizationEntity entity;
    private Result result;
    private World world;

    @Before
    public void setUp() throws Exception {
        world = World.startWithDefaults ( "organization-test" );
        result = new Result ();
        entity = world.actorFor ( Definition.has ( Organization.class, Definition.parameters ( result ) ), OrganizationEntity.class );
    }

    @Test
    public void testApplyDefinedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( OrganizationDefined.class, result.applied.get ( 0 ).getClass () );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.recategorised );
    }

    @Test
    public void testApplyDescribedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.recategorised );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( OrganizationDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "newDesc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( OrganizationDescribed.class, result.applied.get ( 1 ).getClass () );

    }

    @Test
    public void testApplyRenamedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.described );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.recategorised );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( OrganizationDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "newDesc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( OrganizationDescribed.class, result.applied.get ( 1 ).getClass () );
        result.until = TestUntil.happenings ( 2 );
        entity.renameTo ( "newName" );
        result.until.completes ();
        Assert.assertEquals ( 3, result.applied.size () );
        Assert.assertEquals ( OrganizationRenamed.class, result.applied.get ( 2 ).getClass () );

    }

}