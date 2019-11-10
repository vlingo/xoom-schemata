import * as faker from 'faker';

describe('Form Validation Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('does not allow submitting invalid forms', function () {
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()
        let schema = faker.company.catchPhraseNoun()
        let majorMinorVersion = faker.random.number(9) + '.' + faker.random.number(9)
        let patchVersion = faker.random.number(9)
        let prevVersion = majorMinorVersion + '.' + patchVersion
        let currentVersion = majorMinorVersion + '.' + (patchVersion + 1)

        cy.visit('/#/organization')
        cy.contains('button', 'Create').should('be.disabled')

        cy.fillField('Name', orgName)
        cy.contains('button', 'Create').should('be.disabled')

        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/unit')
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Name', unitName)
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/context')
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Namespace', namespace)
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()

        cy.visit('/#/schema')
        cy.fieldContent('SchemaID').should('be.empty')
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Name', schema)
        cy.contains('button', 'Create').should('be.disabled')
        cy.selectOption('Category', faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']))
        cy.contains('button', 'Create').should('be.disabled')
        cy.selectOption('Scope', faker.random.arrayElement(['Public', 'Private']))
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.fieldContent('SchemaID').should('not.be.empty')

        cy.visit('/#/schemaVersion')
        cy.fieldContent('SchemaVersionID').should('be.empty')
        cy.contains('button', 'Create').should('be.disabled')

        cy.fillField('Previous Version', "not a version number").get('.v-messages__message')
        //v-messages__message
        cy.fillField('Previous Version', prevVersion)
        cy.contains('button', 'Create').should('be.disabled')

        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Current Version', "A.b:Florgs").get('.v-messages__message')
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillField('Current Version', currentVersion).get('.v-messages__message')
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillEditor('#description-editor', faker.lorem.sentence())
        cy.contains('button', 'Create').should('be.disabled')
        cy.fillEditor('#specification-editor', 'event SalutationHappened {\n' +
            '    type eventType')
        cy.contains('button', 'Create').click()

        cy.fieldContent('SchemaVersionID').should('not.be.empty')
    });

});