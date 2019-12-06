describe('Schemata View Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can browse schemata', function () {
        this.skip();
        cy.task('schemata:withTestData').then(testData => {
            let data = <Cypress.SchemataTestData><unknown>testData
            cy.visit("/#/schemata")
            cy.expandSchemaTree(data)

            // Select version
            cy.contains('.v-list-item__title', data.version.currentVersion).click()

            // Assert spec & desc
            cy.contains('.v-tab', 'Specification').click()
            cy.editorContent('#specification-editor').should('contain', 'event SalutationHappened')
            cy.editorContent('#specification-editor').should('contain', 'type eventType')

            cy.contains('.v-tab', 'Description').click()
            cy.editorContent('#description-editor').should('contain', data.version.description)
        })
    });

    it('can refresh schemata', function () {
        cy.visit("/#/schemata")
        cy.task('schemata:withTestData').then(testData => {
            let data = <Cypress.SchemataTestData><unknown>testData
            cy.fillField('Search', data.organization.name)
            cy.get(`.v-treeview-node__label:contains(${data.organization.name})`).should('not.exist')
            cy.get('#button-refresh-schemata-tree').click()
            cy.get(`.v-treeview-node__label:contains(${data.organization.name})`).should('exist')
        })
    });
});
