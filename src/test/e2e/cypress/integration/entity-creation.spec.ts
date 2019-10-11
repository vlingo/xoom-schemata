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
        cy.contains('button', 'Create').click()
        cy.fieldContent('OrganizationID').should('not.be.empty')
    });

    it('can create unit', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.commerce.department()

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/unit')
        cy.fieldContent('UnitID').should('be.empty')
        cy.fillField('Name', unitName)
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.fieldContent('UnitID').should('not.be.empty')
    });

    it('can create context', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/unit')
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/context')
        cy.fieldContent('ContextID').should('be.empty')
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.fieldContent('ContextID').should('not.be.empty')
    });

    it('can create schema', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()
        let name = faker.company.catchPhraseDescriptor() + faker.company.catchPhraseNoun()

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/unit')
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/context')
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/schema')
        cy.fieldContent('SchemaID').should('be.empty')
        cy.fillField('Name', name)
        cy.selectOption('Category', faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']))
        cy.selectOption('Scope', faker.random.arrayElement(['Public', 'Private']))
        cy.fillField('Description', faker.lorem.sentence())


        cy.contains('button', 'Create').click()
        cy.fieldContent('SchemaID').should('not.be.empty')
    });

    it('can create schema version', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()
        let schema = faker.company.catchPhraseNoun()
        let majorMinorVersion = faker.random.number(9) + '.' + faker.random.number(9)
        let patchVersion = faker.random.number(9)
        let prevVersion = majorMinorVersion + '.' + patchVersion
        let currentVersion = majorMinorVersion + '.' + (patchVersion + 1)

        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/unit')
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/context')
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/schema')
        cy.fieldContent('SchemaID').should('be.empty')
        cy.fillField('Name', schema)
        cy.selectOption('Category', faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']))
        cy.selectOption('Scope', faker.random.arrayElement(['Public', 'Private']))
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.fieldContent('SchemaID').should('not.be.empty')

        cy.visit('/#/schemaVersion')
        cy.fieldContent('SchemaVersionID').should('be.empty')
        cy.fillField('Previous Version', prevVersion)
        cy.fillField('Current Version', currentVersion)
        cy.selectOption('Status', faker.random.arrayElement(['Draft', 'Published', 'Deprecated', 'Removed']))
        cy.fillEditor('#description-editor', faker.lorem.sentence())
        cy.fillEditor('#specification-editor', 'event SalutationHappened {\n' +
            '    type eventType')
        cy.contains('button', 'Create').click()

        cy.fieldContent('SchemaVersionID').should('not.be.empty')
    });

});