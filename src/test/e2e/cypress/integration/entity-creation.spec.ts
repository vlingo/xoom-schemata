describe('Entity Creation Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('can create organizations', function () {
        cy.visit('/#/organization')
        cy.fieldContent('OrganizationID').should('be.empty')
        cy.fillField('Name', 'Test Organization 1')
        cy.fillField('Description', 'Description for Test Organization 1')
        cy.contains('button','Create').click()
        cy.fieldContent('Organization').should('not.be.empty')
    });

    it('can create units', function () {
        cy.visit('/#/unit')
        cy.fieldContent('UnitID').should('be.empty')
        cy.selectOption('Organization','Test Organization 1')
        cy.fillField('Name', 'Test Organization 1 Unit 1')
        cy.fillField('Description', 'Description for Test Organization 1 Unit 1')
        cy.contains('button','Create').click()
        cy.fieldContent('UnitID').should('not.be.empty')
    });

});