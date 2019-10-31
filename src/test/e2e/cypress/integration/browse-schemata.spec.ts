import * as faker from 'faker';

describe('Schemata View Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });


    it('can browse schemata', function () {
        // Setup
        let orgName = faker.company.companyName()
        let unitName = faker.lorem.word()
        let namespace = faker.internet.domainName()
        let schema = faker.company.catchPhraseNoun()
        let majorMinorVersion = faker.random.number(9) + '.' + faker.random.number(9)
        let patchVersion = faker.random.number(9)
        let prevVersion = majorMinorVersion + '.' + patchVersion
        let currentVersion = majorMinorVersion + '.' + (patchVersion + 1)
        let spec = 'event SalutationHappened {\n' +
            '    type eventType'
        let desc = faker.lorem.sentence()

        // Create Entities
        cy.visit('/#/organization')
        cy.fillField('Name', orgName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()
        cy.contains('a', 'Create Unit').click()

        cy.selectOption('Organization', orgName)
        cy.fillField('Name', unitName)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()
        cy.contains('a', 'Create Context').click()

        cy.selectOption('Organization', orgName)
        cy.selectOption('Unit', unitName)
        cy.fillField('Namespace', namespace)
        cy.fillField('Description', 'foo')
        cy.contains('button', 'Create').click()
        cy.contains('a', 'Create Schema').click()

        cy.fieldContent('SchemaID').should('be.empty')
        cy.selectOption('Organization', orgName)
        cy.selectOption('Unit', unitName)
        cy.selectOption('Context', namespace)
        cy.fillField('Name', schema)
        cy.selectOption('Category', faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']))
        cy.selectOption('Scope', faker.random.arrayElement(['Public', 'Private']))
        cy.fillField('Description', faker.lorem.sentence())
        cy.contains('button', 'Create').click()
        cy.contains('a', 'Create Schema Version').click()

        cy.fieldContent('SchemaVersionID').should('be.empty')
        cy.selectOption('Organization', orgName)
        cy.selectOption('Unit', unitName)
        cy.selectOption('Context', namespace)
        cy.selectOption('Schema', schema)
        cy.fillField('Previous Version', prevVersion)
        cy.fillField('Current Version', currentVersion)
        cy.fillEditor('#description-editor', desc)
        cy.fillEditor('#specification-editor', spec)
        cy.contains('button', 'Create').click()

        // Assert visibility in treeview
        // Filter for Org
        cy.visit("/#/schemata")
            .fillField('Search', orgName)

        // Expand Tree
        cy.contains('.v-treeview-node__label', orgName).click()
        cy.contains('.v-treeview-node__label', unitName).click()
        cy.contains('.v-treeview-node__label', namespace).click()
        cy.contains('.v-treeview-node__label', schema).click()

        // Select version
        cy.contains('.v-list-item__title', currentVersion).click()

        // Assert spec & desc
        cy.editorContent('#specification-editor').should('contain','event SalutationHappened')
        cy.editorContent('#specification-editor').should('contain','type eventType')


        cy.contains('.v-tab', 'Description').click()
        cy.contains('.v-window-item--active',desc)

    });
});