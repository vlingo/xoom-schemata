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
            .contains('.v-toolbar__title.headline','vlingo/schemata')
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

});