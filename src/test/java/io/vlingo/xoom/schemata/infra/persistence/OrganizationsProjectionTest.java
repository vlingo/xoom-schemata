package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationEntity;
import io.vlingo.xoom.schemata.model.OrganizationState;
import io.vlingo.xoom.schemata.query.view.OrganizationsView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static io.vlingo.xoom.schemata.test.Assertions.assertOnNotNull;
import static org.junit.Assert.assertEquals;

public class OrganizationsProjectionTest extends ProjectionTest {
  @Test
  public void itCreatesOrganizationsViewOnOrganizationDefinedEvent() {
    final Completes<OrganizationState> organizationState = givenAnyOrganization();
    final Completes<OrganizationsView> view = organizationState.andThenTo(state -> organizationsView());
    final Completes<OrganizationId> id = organizationState.andThen(state -> state.organizationId);

    assertCompletes(view, id, (organizationsView, organizationId) ->
            assertOnNotNull(organizationsView.get(organizationId.value), (item) -> {
              assertEquals(Fixtures.OrgName, item.name);
              assertEquals(Fixtures.OrgDescription, item.description);
            })
    );
  }

  @Test
  public void itUpdatesOrganizationNameAndDescriptionOnOrganizationRedefinedEvent() {
    final Completes<OrganizationState> organizationState = onceProjected(OrganizationsView.class, () -> givenAnyOrganization()
            .andThenTo(state -> organization(state.organizationId).redefineWith("New VLINGO", "New organization description")));
    final Completes<OrganizationsView> view = organizationState.andThenTo(state -> organizationsView());
    final Completes<OrganizationId> id = organizationState.andThen(state -> state.organizationId);

    assertCompletes(view, id, (organizationsView, organizationId) ->
            assertOnNotNull(organizationsView.get(organizationId.value), (item) -> {
              assertEquals("New VLINGO", item.name);
              assertEquals("New organization description", item.description);
            })
    );
  }

  @Test
  public void itUpdatesOrganizationNameOnOrganizationRenamedEvent() {
    final Completes<OrganizationState> organizationState = onceProjected(OrganizationsView.class, () -> givenAnyOrganization()
            .andThenTo(state -> organization(state.organizationId).renameTo("New VLINGO")));
    final Completes<OrganizationsView> view = organizationState.andThenTo(state -> organizationsView());
    final Completes<OrganizationId> id = organizationState.andThen(state -> state.organizationId);

    assertCompletes(view, id, (organizationsView, organizationId) ->
            assertOnNotNull(organizationsView.get(organizationId.value), (item) ->
                    assertEquals("New VLINGO", item.name)
            )
    );
  }

  public Completes<OrganizationsView> organizationsView() {
    return StorageProvider.instance().organizationQueries.organizations();
  }

  public Organization organization(final OrganizationId organizationId) {
    return stage.actorFor(Organization.class, OrganizationEntity.class, organizationId);
  }
}
