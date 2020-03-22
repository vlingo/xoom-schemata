describe('Schemata View Tests', function () {
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

      cy.contains('.v-tab', 'Specification').click()
      cy.fillEditor('#specification-editor', 'event SalutationHappened {\n' +
          '    type eventType\n'+
          '    string foo\n'
      )
      cy.contains('button', 'Save Specification').click()
      cy.wait(500).contains('button', 'Source').click()

      cy.editorContent('#source-editor').should('contain', 'public final class SalutationHappened')
      cy.editorContent('#source-editor').should('contain', 'String foo') // Reproduces #98
    })
  });
});
