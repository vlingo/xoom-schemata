<template>
    <v-card class="xs12" height="95vh">
        <v-card-title>Edit</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    lazy-validation
            >
                <v-row>
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
                                return-object
                                :loading="loading.schemata"
                                item-value="schemaId"
                                item-text="name"
                                v-model="schema"
                        ></v-select>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="versions"
                                label="Version"
                                return-object
                                item-text="currentVersion"
                                item-value="schemaVersionId"
                                :loading="loading.versions"
                                v-model="version"
                        ></v-select>
                    </v-col>
                </v-row>
                    <v-divider></v-divider>
                <v-row>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="categories"
                                label="Category"
                                :loading="loading.categories"
                                v-model="category"
                        ></v-select>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
    import { mapFields } from 'vuex-map-fields';
    import SchemataRepository from '@/api/SchemataRepository'

    export default {
        data: () => {
            return {
                loading: {
                    organizations: false,
                    units: false,
                    contexts: false,
                    category: false,
                    schemata: false,
                    versions: false
                }
            }
        },
        computed: {
            ...mapFields([
                'organization',
                'unit',
                'context',
                'category',
                'schema',
                'version'
            ]),
        },
        asyncComputed: {
            async organizations() {
                this.loading.organizations = true
                const result = await SchemataRepository.getOrganizations()
                this.loading.organizations = false
                return result
            },
            async units() {
                if(!this.organization) return []

                this.loading.units = true
                const result = await SchemataRepository.getUnits(this.organization.organizationId)
                this.loading.units = false
                return result
            },
            async contexts() {
                if(!this.organization || !this.unit) return []

                this.loading.contexts = true
                const result = await SchemataRepository.getContexts(
                    this.organization.organizationId,
                    this.unit.unitId)
                this.loading.contexts = false
                return result
            },
            async categories() {
                this.loading.categories = true
                const result = await SchemataRepository.getCategories()
                this.loading.categories = false
                return result
            },
            async schemata() {
                if(!this.organization || !this.unit || !this.context) return []

                this.loading.schemata = true
                const result = await SchemataRepository.getSchemata(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId
                )
                this.loading.schemata = false
                return result
            },
            async versions() {
                if(!this.organization || !this.unit || !this.context || !this.schema) return []

                this.loading.versions = true
                const result = await SchemataRepository.getVersions(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId,
                    this.schema.schemaId
                )
                this.loading.versions = false
                return result
            },
        },
        methods: {},

    }
</script>
