import * as faker from 'faker';

describe('Entity Creation Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can create organization', function () {
        let orgName = faker.company.companyName()
        cy.visit('/#/organization')
        cy.fieldContent('OrganizationID').should('be.empty')
        cy.fillField('Name', orgName)
        cy.fillField('Description', faker.company.catchPhrase())
        cy.contains('button','Create').click()
        cy.fieldContent('OrganizationID').should('not.be.empty')
    });

    it('can create unit', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button','Create').click()

        cy.visit('/#/unit')
        cy.fieldContent('UnitID').should('be.empty')
        cy.selectOption('Organization',orgName)
        cy.fillField('Name', unitName)
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button','Create').click()
        cy.fieldContent('UnitID').should('not.be.empty')
    });

    it('can create context', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button','Create').click()

        cy.visit('/#/unit')
        cy.selectOption('Organization',orgName)
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button','Create').click()

        cy.visit('/#/context')
        cy.fieldContent('ContextID').should('be.empty')
        cy.selectOption('Organization',orgName)
        cy.selectOption('Unit',unitName)
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button','Create').click()
        cy.fieldContent('ContextID').should('not.be.empty')


    });

});