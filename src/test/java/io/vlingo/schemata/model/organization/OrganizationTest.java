package io.vlingo.schemata.model.organization;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.schemata.model.sourcing.Result;
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
    public void applyDefinedVlingoSchemata() throws Exception {
    }

    @Test
    public void applyDescribedVlingoSchemata() throws Exception {
    }

    @Test
    public void applyRenamedVlingoSchemata() throws Exception {
    }

}