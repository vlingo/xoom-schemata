import * as faker from 'faker';

describe('Entity Creation Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can create all entities', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()
        let schema = faker.company.catchPhraseNoun()
        let majorMinorVersion = faker.random.number(9) + '.' + faker.random.number(9)
        let patchVersion = faker.random.number(9)
        let currentVersion = majorMinorVersion + '.' + (patchVersion + 1)

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.navigateTo("Unit")
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.navigateTo("Context")
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.navigateTo("Schema")
        cy.fieldContent('SchemaID').should('be.empty')
        cy.fillField('Name', schema)
        cy.selectOption('Category', faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']))
        cy.selectOption('Scope', faker.random.arrayElement(['Public', 'Private']))
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.fieldContent('SchemaID').should('not.be.empty')

        cy.navigateTo("Schema Version")
        cy.fieldContent('SchemaVersionID').should('be.empty')
        cy.fillField('Previous Version', '0.0.0')
        cy.fillField('Current Version', currentVersion)
        cy.fillEditor('#description-editor', faker.lorem.sentence())
        cy.fillEditor('#specification-editor', 'event SalutationHappened {\n' +
            '    type eventType')
        cy.contains('button', 'Create').click()

        cy.fieldContent('SchemaVersionID').should('not.be.empty')
    });

});
