package io.vlingo.schemata.model.schema;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.schemata.model.EventsCategory;
import io.vlingo.schemata.model.events.schemaevent.SchemaDefined;
import io.vlingo.schemata.model.events.schemaevent.SchemaDescribed;
import io.vlingo.schemata.model.events.schemaevent.SchemaRecategorized;
import io.vlingo.schemata.model.events.schemaevent.SchemaRenamed;
import io.vlingo.schemata.model.sourcing.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaTest {
    private SchemaEntity entity;
    private Result result;
    private World world;

    @Before
    public void setUp() throws Exception {
        world = World.startWithDefaults ( "schema-test" );
        result = new Result ();
        entity = world.actorFor ( Definition.has ( Schema.class, Definition.parameters ( result ) ), SchemaEntity.class );
    }

    @Test
    public void testApplyDefinedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( SchemaDefined.class, result.applied.get ( 0 ).getClass () );
        Assert.assertFalse ( result.recategorised );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
    }

    @Test
    public void testApplyDescribedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.recategorised );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( SchemaDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "newDesc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( SchemaDescribed.class, result.applied.get ( 1 ).getClass () );

    }

    @Test
    public void testApplyRecategorizedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.recategorised );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( SchemaDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "newDesc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( SchemaDescribed.class, result.applied.get ( 1 ).getClass () );
        result.until = TestUntil.happenings ( 2 );
        entity.recategorizedAs ( EventsCategory.Category.Commands );
        result.until.completes ();
        Assert.assertEquals ( 3, result.applied.size () );
        Assert.assertEquals ( SchemaRecategorized.class, result.applied.get ( 2 ).getClass () );
    }

    @Test
    public void testApplyRenamedVlingoSchemata() throws Exception {
        result.until.completes ();
        Assert.assertTrue ( result.defined );
        Assert.assertFalse ( result.recategorised );
        Assert.assertFalse ( result.renamed );
        Assert.assertFalse ( result.described );
        Assert.assertEquals ( 1, result.applied.size () );
        Assert.assertEquals ( SchemaDefined.class, result.applied.get ( 0 ).getClass () );
        result.until = TestUntil.happenings ( 1 );
        entity.describeAs ( "newDesc" );
        result.until.completes ();
        Assert.assertEquals ( 2, result.applied.size () );
        Assert.assertEquals ( SchemaDescribed.class, result.applied.get ( 1 ).getClass () );
        result.until = TestUntil.happenings ( 2 );
        entity.recategorizedAs ( EventsCategory.Category.Commands );
        result.until.completes ();
        Assert.assertEquals ( 3, result.applied.size () );
        Assert.assertEquals ( SchemaRecategorized.class, result.applied.get ( 2 ).getClass () );
        result.until = TestUntil.happenings ( 3 );
        entity.renameTo ( "newName" );
        result.until.completes ();
        Assert.assertEquals ( 4, result.applied.size () );
        Assert.assertEquals ( SchemaRenamed.class, result.applied.get ( 3 ).getClass () );
    }

}