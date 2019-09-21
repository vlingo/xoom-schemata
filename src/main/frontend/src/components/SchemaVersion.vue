<template>
    <v-card class="xs12" height="95vh" width="100%">
        <v-card-title>Schema Version</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    lazy-validation
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="schemaVersionId"
                                label="SchemaVersionID"
                                disabled
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="organizations"
                                label="Organization"
                                :loading="loading.organizations"
                                return-object
                                item-value="organizationId"
                                item-text="name"
                                v-model="organization"

                        ></v-select>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="units"
                                label="Unit"
                                :loading="loading.units"
                                return-object
                                item-value="unitId"
                                item-text="name"
                                v-model="unit"

                        ></v-select>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="contexts"
                                label="Context"
                                :loading="loading.contexts"
                                return-object
                                item-value="contextId"
                                item-text="namespace"
                                v-model="context"
                        ></v-select>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="schemata"
                                label="Schema"
                                :loading="loading.schema"
                                return-object
                                item-value="schemaId"
                                item-text="name"
                                v-model="schema"
                        ></v-select>
                    </v-col>


                    <v-col class="d-flex" cols="4">
                        <v-text-field
                                v-model="previousVersion"
                                label="Previous Version"
                                required
                        ></v-text-field>
                    </v-col>

                    <v-col class="d-flex" cols="4">
                        <v-text-field
                                v-model="currentVersion"
                                label="Current Version"
                                required
                        ></v-text-field>
                    </v-col>

                    <v-col class="d-flex" cols="4">
                        <v-select
                                :items="statuses"
                                label="Status"
                                v-model="status"
                        ></v-select>
                    </v-col>

                    <v-col class="d-flex" cols="12" lg="6">
                        <v-textarea
                                v-model="description"
                                label="Description"
                                required
                        ></v-textarea>
                    </v-col>
                    <v-col class="d-flex" cols="12" lg="6">
                        <v-textarea
                                v-model="specification"
                                label="Specification"
                                required
                        ></v-textarea>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   :disabled="!status || !previousVersion || !currentVersion ||!description ||!specification || !description || !organization || !unit || !context || !schema"
                   @click="createSchemaVersion">Create
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import {mapFields} from 'vuex-map-fields';
    import Repository from '@/api/SchemataRepository'

    export default {
        data: () => {
            return {
                schemaVersionId: '',
                description: '',
                specification: '',
                status: 'Draft',
                previousVersion: '',
                currentVersion: '',
                statuses: ['Draft', 'Published', 'Deprecated', 'Removed'],

                loading: {
                    organizations: false,
                    units: false,
                    contexts: false,
                    versions: false,
                    schemata: false,
                }
            }
        },
        computed: {
            ...mapFields([
                'organization',
                'unit',
                'context',
                'schema',
                'version'
            ]),
        },
        watch: {
            description() {
                this.schemaVersionId = ''
            },
            specification() {
                this.schemaVersionId = ''
            },
            status() {
                this.schemaVersionId = ''
            },
            previousVersion() {
                this.schemaVersionId = ''
            },
            currentVersion() {
                this.schemaVersionId = ''
            }
        },

        asyncComputed: {
            async organizations() {
                this.loading.organizations = true
                const result = await Repository.getOrganizations()
                this.loading.organizations = false
                return result
            },
            async units() {
                if (!this.organization) return []

                this.loading.organizations = true
                const result = await Repository.getUnits(this.organization.organizationId)
                this.loading.organizations = false
                return result
            },
            async contexts() {
                if (!this.organization || !this.unit) return []

                this.loading.contexts = true
                const result = await Repository.getContexts(
                    this.organization.organizationId,
                    this.unit.unitId
                )
                this.loading.contexts = false
                return result
            },
            async schemata() {
                if (!this.organization || !this.unit || !this.context) return []

                this.loading.schemata = true
                const result = await Repository.getSchemata(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId,
                )

                this.loading.schemata = false
                return result
            },

        },

        methods: {
            createSchemaVersion() {
                let vm = this
                Repository.createSchemaVersion(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId,
                    this.schema.schemaId,
                    this.speficication,
                    this.description,
                    this.status,
                    this.previousVersion,
                    this.currentVersion)
                    .then((created) => {
                            vm.version = created
                            vm.schemaVersionId = created.schemaVersionId
                            vm.speficication = created.speficication
                            vm.description = created.description
                            vm.status = created.status
                            vm.previousVersion = created.previousVersion
                            vm.currentVersion = created.currentVersion
                        }
                    )
                    .catch(function (err) {
                        let response = err.response ? err.response.data + ' - ' : ''
                        vm.$store.commit('raiseError', {message: response + err})
                    })
            }
        }
    }
</script>
