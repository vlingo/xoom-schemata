describe('Basic Application Tests', function () {
    before(() => {
    });

    after(() => {

    });

    beforeEach(() => {
        cy.viewport(1280, 960)
    });

    it('shows application layout', function () {
        cy.visit('/');

    });

});