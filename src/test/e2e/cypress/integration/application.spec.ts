describe('Basic Application Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('shows application layout', function () {
        cy.visit('/')
            .contains('.v-toolbar__title.headline', 'vlingo/schemata')
            .get('.v-navigation-drawer__content').should('be.visible')
            .get('#schemata-treeview').should('be.visible')
            .get('#schemata-versions').should('be.visible')
            .get('#schemata-properties').should('be.visible')
    });

    it('can toggle menu', function () {
        cy.visit('/')
            .get('.v-navigation-drawer__content').should('be.visible')
            .get('.v-list-item__title').should('not.be.visible')
            .get('#schemata-navigation-toggle').click()
            .get('.v-list-item__title').should('be.visible')
            .get('#schemata-navigation-toggle').click()
            .get('.v-list-item__title').should('not.be.visible')
    });

    it('toggles menu on hover', function () {
        cy.visit('/')
            .get('.v-list-item__title').should('not.be.visible')
            .get('.v-navigation-drawer__content')
            .trigger('mouseenter')
            .trigger('mouseover')
            .get('.v-list-item__title').should('be.visible')
            .get('.v-navigation-drawer__content')
            .trigger('mouseleave')
            .get('.v-list-item__title').should('not.be.visible')
    });

    it('can open all creation views', function () {
        cy.visit('/')
            .get('#schemata-navigation-toggle').click()

        cy.contains('.v-list-item__title', 'New Organization').click()
            .get('#schemata-view-organization').contains('label','OrganizationID')
        cy.contains('.v-list-item__title', 'New Unit').click()
            .get('#schemata-view-unit').contains('label','UnitID')
        cy.contains('.v-list-item__title', 'New Context').click()
            .get('#schemata-view-context').contains('label','ContextID')
        cy.contains('.v-list-item__title', 'New Schema Version').click()
            .get('#schemata-view-schema-version').contains('label','SchemaVersionID')
        cy.contains('.v-list-item__title', 'New Schema').click()
            .get('#schemata-view-schema').contains('label','SchemaID')
    });

});