describe('Code Generation Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can generate java code', function () {
        cy.task('schemata:withTestData').then(testData => {
            let data = <Cypress.SchemataTestData><unknown>testData

            cy.visit("/#/schemata")
            cy.expandSchemaTree(data)

            // Select version
            cy.contains('.v-list-item__title', data.version.currentVersion).click()

            cy.contains('button', 'Source').click()
            cy.editorContent('#source-editor').should('contain', 'public final class SalutationHappened')

        })
    });
});
