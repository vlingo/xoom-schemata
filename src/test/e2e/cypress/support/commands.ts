// ***********************************************
// This adds custom commands to cypress.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

Cypress.Commands.add("fillField", (label: string, text: string) => {
    cy.contains('label', new RegExp("^" + label + "$"))
        .next('input,textarea')
        .clear()
        .type(text)
})

Cypress.Commands.add("fillEditor", (id: string, text: string) => {
    cy.get(id)
        .click()
        .focused()
        .type('{ctrl}a')
        .type(text)
})

Cypress.Commands.add("fieldContent", (label: string) => {
    cy.contains('label', new RegExp("^" + label + "$"))
        .next('input,textarea').invoke('val')
})

Cypress.Commands.add("editorContent", (id: string) => {
    cy.get(id).within(($editor) => {
      cy.get('.monaco-editor .lines-content')
        .then(content => content.text()
          .replace(/\s+/g, ' ')
          .trim()
        )
    })
})

Cypress.Commands.add("selectOption", (label: string, optionLabel: string) => {
    cy.contains('label', new RegExp("^" + label + "$"))
        .next('input')
        .type(optionLabel.substr(0, optionLabel.length - 1))
        .get('.v-select-list')
        .contains('.v-list-item', optionLabel)
        .click({force: true})
})

Cypress.Commands.add("expandSchemaTree", (data: Cypress.SchemataTestData) => {
    cy.fillField('Search', data.organization.name)
    cy.contains('.v-treeview-node__label', data.organization.name).click()
    cy.contains('.v-treeview-node__label', data.unit.name).click()
    cy.contains('.v-treeview-node__label', data.context.namespace).click()
    cy.contains('.v-treeview-node__label', data.schema.name).click()
})
