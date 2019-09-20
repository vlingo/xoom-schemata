<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="95vh" min-width="100%">
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
                                :items="categories"
                                label="Category"
                                :loading="loading.categories"
                                item-value="id"
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
                }
            }
        },
        computed: {
            ...mapFields([
                'organization',
                'unit',
                'context',
                'category'
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
        },
        methods: {},

    }
</script>
