/// <reference types="cypress" />

declare namespace Cypress {
    interface SchemataTestData {
        organization: any;
        unit: any;
        context: any;
        schema: any;
        version: any;
    }

    interface Chainable {
        /**
         * Fill a Vuetify text(area) field identified by its label
         * @example cy.fillField('Name','Zaphod Beeblebrox')
         */
        fillField(label: string, text: string): Chainable<Element>

        /**
         * Fill a vue-monaco-editor field identified by its id
         * @example cy.fillEditor('#specification-editor','schema MySpec {}')
         */
        fillEditor(label: string, text: string): Chainable<Element>

        /**
         * Get the contents of a vue-monaco-editor field identified by its id.
         * Note that whitespace is simplified, all consecutive whitespaces are reduced to one
         * and leading and trailing whitespace is removed.
         * @example cy.editorContent('#specification-editor')
         */
        editorContent(id: string): Chainable<Element>

        /**
         * Return the value a Vuetify text(area) field identified by its label
         * @example cy.fieldContent('Name')
         */
        fieldContent(label: string): Chainable<Element>

        /**
         * Select an option from a Vuetify _autocomplete_ select box field identified by their labels
         * @example cy.selectOption('Color','Blue')
         */
        selectOption(label: string, OptionLabel: string): Chainable<Element>

        /**
         * Expand complete schema hierarchy tree for the given data.
         * Requires you to currently visit a view containing the browse schemata vuetify treeview.
         * @example
         *   cy.task('schemata:withTestData').then(testData => {
         *     let data = <Cypress.SchemataTestData><unknown>testData
         *     cy.expandSchemaTree(data)
         *   }
         */
        expandSchemaTree(data: Cypress.SchemataTestData): Chainable<Element>

    }
}