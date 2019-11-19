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

      cy.selectFromTree(data, data.organization.name)


      cy.navigateTo('Organization')
      cy.fillField('Name', data.organization.name + ' - updated')
      cy.fillField('Description', data.organization.description + ' - updated')
      cy.contains('button', 'Save').click()

      cy.selectFromTree(data, data.organization.name)

      cy.navigateTo('Organization')
      cy.fieldContent('Name').should('contain','- updated')
      cy.fieldContent('Description').should('contain','- updated')
    })
  });

  it('can update unit', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.selectFromTree(data, data.unit.name)

      cy.navigateTo('Unit')
      cy.fillField('Name', data.unit.name + ' - updated')
      cy.fillField('Description', data.unit.description + ' - updated')
      cy.contains('button', 'Save').click()

      cy.selectFromTree(data, data.unit.name)

      cy.navigateTo('Unit')
      cy.fieldContent('Name').should('contain','- updated')
      cy.fieldContent('Description').should('contain','- updated')
    })
  });

  it('can update context', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.selectFromTree(data, data.context.namespace)

      cy.navigateTo('Context')
      cy.fillField('Namespace', data.context.namespace + ' - updated')
      cy.fillField('Description', data.context.description + ' - updated')
      cy.contains('button', 'Save').click()

      cy.selectFromTree(data, data.context.namespace)

      cy.navigateTo('Context')
      cy.fieldContent('Namespace').should('contain','- updated')
      cy.fieldContent('Description').should('contain','- updated')
    })
  });

  it('can update schema', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.selectFromTree(data, data.schema.name)

      cy.navigateTo('Schema')
      cy.fillField('Name', data.schema.name + ' - updated')
      cy.fillField('Description', data.schema.description + ' - updated')
      cy.contains('button', 'Save').click()

      cy.selectFromTree(data, data.schema.name)

      cy.navigateTo('Schema')
      cy.fieldContent('Name').should('contain','- updated')
      cy.fieldContent('Description').should('contain','- updated')
    })
  });
});
