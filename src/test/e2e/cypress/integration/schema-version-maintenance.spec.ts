describe('Schemata View Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can update schema version state', function () {
        cy.task('schemata:withTestData').then(testData => {
            let data = <Cypress.SchemataTestData><unknown>testData

            cy.visit("/#/schemata")
            cy.expandSchemaTree(data)

            // Select version
            cy.contains('.v-list-item__title', data.version.currentVersion).click()

            // Assert all states reachable
            cy.contains('button', 'Publish').should('be.enabled')
            cy.contains('button', 'Deprecate').should('be.enabled')
            cy.contains('button', 'Remove').should('be.enabled')
            cy.get('.v-chip.warning')
            cy.contains('button', 'Save').should('be.enabled')

            //Publish
            cy.contains('button', 'Publish').click()
            cy.contains('button', 'Publish').should('be.disabled')
            cy.contains('button', 'Deprecate').should('be.enabled')
            cy.contains('button', 'Remove').should('be.disabled')
            cy.contains('button', 'Save').should('be.disabled')

            //Deprecate
            cy.contains('button', 'Deprecate').click()
            cy.contains('button', 'Publish').should('be.disabled')
            cy.contains('button', 'Deprecate').should('be.disabled')
            cy.contains('button', 'Remove').should('be.enabled')
            cy.get('.v-chip.warning')
            cy.contains('button', 'Save').should('be.disabled')

            //Remove
            cy.contains('button', 'Remove').click()
            cy.contains('button', 'Publish').should('be.disabled')
            cy.contains('button', 'Deprecate').should('be.disabled')
            cy.contains('button', 'Remove').should('be.disabled')
            cy.get('.v-chip.warning')
            cy.contains('button', 'Save').should('be.disabled')

        })
    });

    it('can update schema version specification', function () {
        cy.task('schemata:withTestData').then(testData => {
            let data = <Cypress.SchemataTestData><unknown>testData

            cy.visit("/#/schemata")
            cy.expandSchemaTree(data)

            // Select version
            cy.contains('.v-list-item__title', data.version.currentVersion).click()

            // Change spec
            cy.fillEditor('#specification-editor', 'foo bar baz')
            cy.contains('button', 'Save').click()

            // Reload and navigate to schema version
            cy.reload()
            cy.visit("/#/schemata")
            cy.expandSchemaTree(data)
            cy.contains('.v-list-item__title', data.version.currentVersion).click()

            cy.editorContent('#specification-editor').should('contain', 'foo bar baz')
        });
    });
});