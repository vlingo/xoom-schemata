// ***********************************************
// This adds custom commands to cypress.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

Cypress.Commands.add("fillField", (label: string, text: string) => {
    cy.contains('label', new RegExp("^"+label+"$"))
        .next('input,textarea').type(text)
})

Cypress.Commands.add("fieldContent", (label: string) => {
    cy.contains('label', new RegExp("^"+label+"$"))
        .next('input,textarea').invoke('val')
})

Cypress.Commands.add("selectOption", (label: string, optionLabel: string) => {
    cy.contains('label', new RegExp("^"+label+"$"))
        .next('input')
        .type(optionLabel.substr(0,optionLabel.length-1))
        .get('.v-select-list')
        .contains('.v-list-item',optionLabel)
        .click({force: true})
})
