describe('Schemata View Tests', function () {
  before(() => {
  });

  after(() => {

  });

  beforeEach(() => {
    cy.viewport(1280, 960)
  });

  it('can update schema version state', function () {

    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select version
      cy.contains('.v-list-item__title', data.version.currentVersion).click()

      // Assert all states reachable
      cy.contains('button', 'Publish').should('be.enabled')
      cy.contains('button', 'Deprecate').should('be.enabled')
      cy.contains('button', 'Remove').should('be.enabled')
      cy.get('.v-chip.warning')
      cy.contains('button', 'Save').should('be.enabled')

      //Publish
      cy.contains('button', 'Publish').click()
      cy.contains('button', 'Publish').should('be.disabled')
      cy.contains('button', 'Deprecate').should('be.enabled')
      cy.contains('button', 'Remove').should('be.disabled')
      cy.contains('button', 'Save').should('be.disabled')

      //Deprecate
      cy.contains('button', 'Deprecate').click()
      cy.contains('button', 'Publish').should('be.disabled')
      cy.contains('button', 'Deprecate').should('be.disabled')
      cy.contains('button', 'Remove').should('be.enabled')
      cy.get('.v-chip.warning')
      cy.contains('button', 'Save').should('be.disabled')

      //Remove
      cy.contains('button', 'Remove').click()
      cy.contains('button', 'Publish').should('be.disabled')
      cy.contains('button', 'Deprecate').should('be.disabled')
      cy.contains('button', 'Remove').should('be.disabled')
      cy.get('.v-chip.warning')
      cy.contains('button', 'Save').should('be.disabled')

    })
  });

  it('can update schema version specification', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select version
      cy.contains('.v-list-item__title', data.version.currentVersion).click()

      // Change spec
      cy.get('div[data-testid="tab-specification"]').click().wait(250)
      cy.fillEditor('#specification-editor', 'foo bar baz')
      cy.contains('button', 'Save Specification').click()

      // Reload and navigate to schema version
      cy.reload()
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)
      cy.contains('.v-list-item__title', data.version.currentVersion).click()

      cy.get('div[data-testid="tab-specification"]').click().wait(250)
      cy.editorContent('#specification-editor').should('contain', 'foo bar baz')
    });
  });

  it('can update schema version description', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData

      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select version
      cy.contains('.v-list-item__title', data.version.currentVersion).click()

      // Change description
      cy.get('div[data-testid="tab-description"]').click().wait(250)
      cy.fillEditor('#description-editor', 'foo bar baz')
      cy.contains('button', 'Save Description').click()

      // Reload and navigate to schema version
      cy.reload()
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)
      cy.contains('.v-list-item__title', data.version.currentVersion).click()

      cy.get('div[data-testid="tab-description"]').click().wait(250)
      cy.editorContent('#description-editor').should('contain', 'foo bar baz')
    });
  });

  it('can create compatible patch version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextPatch = SemanticVersion.from(data.version.currentVersion).nextPatch().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextPatch)

      cy.fillEditor('#description-editor', `compatible patch **${nextPatch}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.compatibleSpecification)

      // Create new version and assert success
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.success').contains(nextPatch)
    });
  });

  it('can create compatible minor version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextMinor = SemanticVersion.from(data.version.currentVersion).nextMinor().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextMinor)

      cy.fillEditor('#description-editor', `compatible minor version **${nextMinor}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.compatibleSpecification)

      // Create new version and assert success
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.success').contains(nextMinor)
    });
  });

  it('can create compatible major version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextMajor = SemanticVersion.from(data.version.currentVersion).nextMajor().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextMajor)

      cy.fillEditor('#description-editor', `compatible major version **${nextMajor}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.compatibleSpecification)

      // Create new version and assert success
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.success').contains(nextMajor)
    });
  });

  it('cannot create incompatible patch version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextPatch = SemanticVersion.from(data.version.currentVersion).nextPatch().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextPatch)

      cy.fillEditor('#description-editor', `_in_compatible patch **${nextPatch}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.incompatibleSpecification)

      // Create new version and assert error
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.error').contains('Incompatible')
    });
  });

  it('cannot create incompatible minor version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextMinor = SemanticVersion.from(data.version.currentVersion).nextMinor().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextMinor)

      cy.fillEditor('#description-editor', `_in_compatible minor version **${nextMinor}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.incompatibleSpecification)

      // Create new version and assert error
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.error').contains('Incompatible')
    });
  });

  it('can create incompatible major version', function () {
    cy.task('schemata:withTestData').then(testData => {
      let data = <Cypress.SchemataTestData><unknown>testData
      let nextMajor = SemanticVersion.from(data.version.currentVersion).nextMajor().format();
      cy.visit("/#/schemata")
      cy.expandSchemaTree(data)

      // Select schema and open schema version view
      cy.contains('.v-treeview-node__label', data.schema.name).click()
      cy.get('[data-testid="Schema Version"]').click()

      // Add schema version content
      cy.fillField('Previous Version', data.version.currentVersion)
      cy.fillField('Current Version', nextMajor)

      cy.fillEditor('#description-editor', `_in_compatible major version **${nextMajor}** for *${data.version.currentVersion}*`)
      cy.fillEditor('#specification-editor', data.version.incompatibleSpecification)

      // Create new version and assert success
      cy.contains('button', 'Create').click()
      cy.get('.v-snack__wrapper.success').contains(nextMajor)
    });
  });
});


class SemanticVersion {
  static versionPattern = /(\d+)\.(\d+)\.(\d+)/

  major: number = 0;
  minor: number = 0;
  patch: number = 0;

  static from(version: string): SemanticVersion {
    let [, major, minor, patch] = this.versionPattern.exec(version) || []
    let result = new SemanticVersion()
    result.major = parseInt(major)
    result.minor = parseInt(minor)
    result.patch = parseInt(patch)
    return result
  }

  clone(): SemanticVersion {
    let result = new SemanticVersion()
    result.major = this.major
    result.minor = this.minor
    result.patch = this.patch
    return result
  }

  format(): string {
    return `${this.major}.${this.minor}.${this.patch}`
  }

  nextPatch(): SemanticVersion {
    let clone = this.clone();
    clone.patch = ++clone.patch
    return clone
  }

  nextMinor(): SemanticVersion {
    let clone = this.clone();
    clone.minor = ++clone.minor
    clone.patch = 0
    return clone
  }

  nextMajor(): SemanticVersion {
    let clone = this.clone();
    clone.major = ++clone.major
    clone.minor = 0
    clone.patch = 0
    return clone
  }

}

