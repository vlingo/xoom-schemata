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

        cy.visit(`/#/schemata/`)
        cy.fillField('Search', data.organization.name)
        cy.contains('.v-treeview-node__label', data.organization.name).click()

        cy.visit(`/#/organization/`)
        cy.fillField('Name', data.organization.name + ' - updated')
        cy.fillField('Description', data.organization.description + ' - updated')
        cy.contains('button', 'Save').click()

        cy.visit(`/#/schemata/`)
        cy.fillField('Search', data.organization.name)
        cy.contains('.v-treeview-node__label', data.organization.name).click()

        cy.visit(`/#/organization/`)
        cy.fieldContent('Name').should('contain','- updated')
        cy.fieldContent('Description').should('contain','- updated')
      })
    });

});
