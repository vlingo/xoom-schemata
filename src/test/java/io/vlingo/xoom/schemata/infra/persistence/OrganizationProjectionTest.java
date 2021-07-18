package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Organization;
import io.vlingo.xoom.schemata.model.OrganizationEntity;
import io.vlingo.xoom.schemata.model.OrganizationState;
import io.vlingo.xoom.schemata.query.view.OrganizationView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class OrganizationProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesOrganizationViewOnOrganizationDefinedEvent() {
    Completes<OrganizationView> view = givenAnyOrganization().andThenTo(state -> organizationView(state.organizationId));

    assertCompletes(view, (organizationView -> {
      assertEquals(Fixtures.OrgName, organizationView.name());
      assertEquals(Fixtures.OrgDescription, organizationView.description());
    }));
  }

  @Test
  public void itUpdatesOrganizationDescriptionOnOrganizationDefinedEvent() {
    Completes<OrganizationState> organizationState = onceProjected(OrganizationView.class, () -> givenAnyOrganization()
            .andThenTo(state -> organization(state.organizationId).describeAs("New organization description")));
    Completes<OrganizationView> view = organizationState.andThenTo(state -> organizationView(state.organizationId));

    assertCompletes(view, (organizationView) -> {
      assertEquals("New organization description", organizationView.description());
    });
  }

  @Test
  public void itUpdatesOrganizationNameOnOrganizationRenamedEvent() {
    Completes<OrganizationState> organizationState = onceProjected(OrganizationView.class, () -> givenAnyOrganization()
            .andThenTo(state -> organization(state.organizationId).renameTo("New VLINGO")));
    Completes<OrganizationView> view = organizationState.andThenTo(state -> organizationView(state.organizationId));

    assertCompletes(view, (organizationView) -> {
      assertEquals("New VLINGO", organizationView.name());
    });
  }

  @Test
  public void itUpdatesOrganizationNameAndDescriptionOnOrganizationRedefinedEvent() {
    Completes<OrganizationState> organizationState = onceProjected(OrganizationView.class, () -> givenAnyOrganization()
            .andThenTo(state -> organization(state.organizationId).redefineWith("New VLINGO", "New description")));
    Completes<OrganizationView> view = organizationState.andThenTo(state -> organizationView(state.organizationId));

    assertCompletes(view, (organizationView) -> {
      assertEquals("New VLINGO", organizationView.name());
      assertEquals("New description", organizationView.description());
    });
  }

  public Completes<OrganizationView> organizationView(OrganizationId organizationId) {
    return StorageProvider.instance().organizationQueries.organization(organizationId.value);
  }

  public Organization organization(OrganizationId organizationId) {
    return stage.actorFor(Organization.class, OrganizationEntity.class, organizationId);
  }
}
