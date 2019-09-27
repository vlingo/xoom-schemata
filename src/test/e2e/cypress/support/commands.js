// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add("fillField", (label, text) => {
    cy.contains('label', label)
        .siblings('input,textarea').type(text)
})

Cypress.Commands.add("fieldContent", (label) => {
    cy.contains('label', label)
        .siblings('input,textarea').invoke('val')
})

Cypress.Commands.add("selectOption", (label, optionLabel) => {
    cy.contains('label', label)
        .siblings('.v-select__selections')
        .click()
        .get('.v-select-list')
        .contains('.v-list-item',optionLabel).click()
})
