import * as faker from 'faker';

describe('Entity Update Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can update organization', function () {
      cy.task('schemata:withTestData').then(testData => {
        let data = <Cypress.SchemataTestData><unknown>testData

        cy.visit(`/#/organization/${data.organization.organizationId}`)
        cy.fillField('Name', data.organization.name + ' - updated')
        cy.fillField('Description', data.organization.description + ' - updated')
        cy.contains('button', 'Save').click()
        cy.reload()
        cy.fieldContent('Name').should('contain','- updated')
        cy.fieldContent('Description').should('contain','- updated')
      })
    });

});
