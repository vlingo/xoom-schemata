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

});