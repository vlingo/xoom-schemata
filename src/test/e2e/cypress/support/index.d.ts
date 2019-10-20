/// <reference types="cypress" />

declare namespace Cypress {
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
         * Return the value a Vuetify text(area) field identified by its label
         * @example cy.fieldContent('Name')
         */
        fieldContent(label: string): Chainable<Element>

        /**
         * Select an option from a Vuetify _autocomplete_ select box field identified by their labels
         * @example cy.selectOption('Color','Blue')
         */
        selectOption(label: string, OptionLabel: string): Chainable<Element>

    }
}