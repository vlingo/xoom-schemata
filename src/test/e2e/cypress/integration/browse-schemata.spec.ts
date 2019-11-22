describe('Schemata View Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can browse schemata', function () {
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
});
