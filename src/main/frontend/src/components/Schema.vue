<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-schema">
        <v-card-title>Schema</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    lazy-validation
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="schemaId"
                                label="SchemaID"
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
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="name"
                                label="Name"
                                required
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="categories"
                                label="Category"
                                :loading="loading.categories"
                                v-model="category"
                        ></v-select>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="scopes"
                                label="Scope"
                                :loading="loading.scopes"
                                v-model="scope"
                        ></v-select>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-textarea
                                v-model="description"
                                label="Description"
                                required
                        ></v-textarea>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   :disabled="!name || !description || !organization || !unit || !context"
                   @click="createSchema">Create
            </v-btn>
            <v-btn color="secondary" to="/schemaVersion"
                   :disabled="!schemaId">Create Schema Version
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
                schemaId: '',
                name: '',
                description: '',
                category: '',
                scope: '',
                loading: {
                    organizations: false,
                    units: false,
                    contexts: false,
                    categories: false,
                    scopes: false
                }
            }
        },
        computed: {
            ...mapFields([
                'organization',
                'unit',
                'context',
                'schema'
            ]),
        },
        watch: {
            name() {
                this.schemaId = ''
            },
            description() {
                this.schemaId = ''
            },
            category() {
                this.schemaId = ''
            },
            scope() {
                this.schemaId = ''
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
            async categories() {
                this.loading.categories = true
                const result = await Repository.getCategories()
                this.loading.categories = false
                return result
            },
            async scopes() {
                this.loading.scopes = true
                const result = await Repository.getScopes()
                this.loading.scopes = false
                return result
            },
        },

        methods: {
            createSchema() {
                let vm = this
                Repository.createSchema(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId,
                    this.name,
                    this.scope,
                    this.category,
                    this.description)
                    .then((created) => {
                            vm.schema = created
                            vm.schemaId = created.schemaId
                            vm.name = created.name
                            vm.scope = created.scope
                            vm.category = created.category
                            vm.description = created.description
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
