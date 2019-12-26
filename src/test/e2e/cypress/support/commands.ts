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
    cy.get(id)
      .should('be.visible')
      .within(($editor) => {
      cy.wait(500).get('.monaco-editor .lines-content')
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
    cy.contains('.v-treeview-node__label', data.organization.name).parent().parent().children('.v-treeview-node__toggle').click({force: true})
    cy.contains('.v-treeview-node__label', data.unit.name).parent().parent().children('.v-treeview-node__toggle').click({force: true})
    cy.contains('.v-treeview-node__label', data.context.namespace).parent().parent().children('.v-treeview-node__toggle').click({force: true})
    cy.contains('.v-treeview-node__label', data.schema.name).click({force: true})
})

Cypress.Commands.add("navigateTo", (label: string) => {
  cy.get(`a[data-testid="${label}"]`).click()
})

Cypress.Commands.add("selectFromTree", (data: Cypress.SchemataTestData, label: string) => {
  cy.visit(`/#/schemata/`)
  cy.expandSchemaTree(data)
  cy.contains('.v-treeview-node__label', label).click({force: true})
})
