<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-context">
        <v-card-title>Context</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    lazy-validation
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="contextId"
                                label="ContextID"
                                disabled
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="organizations"
                                label="Organization"
                                :loading="loading.organizations"
                                return-object
                                item-value="organizationId"
                                item-text="name"
                                v-model="organization"

                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="units"
                                label="Unit"
                                :loading="loading.units"
                                return-object
                                item-value="unitId"
                                item-text="name"
                                v-model="unit"

                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="namespace"
                                label="Namespace"
                                required
                        ></v-text-field>
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
                   :disabled="!namespace || !description || !organization||!unit"
                   @click="createUnit">Create
            </v-btn>
            <v-btn color="secondary" to="/schema"
                   :disabled="!contextId">Create Schema
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
                contextId: '',
                namespace: '',
                description: '',
                loading: {
                    organizations: false,
                    units: false
                }
            }
        },
        computed: {
            ...mapFields([
                'organization',
                'unit',
                'context'
            ]),
        },
        watch: {
            namespace() {
                this.contextId = ''
            },
            description() {
                this.contextId = ''
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
        },

        methods: {
            createUnit() {
                let vm = this
                Repository.createContext(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.namespace,
                    this.description)
                    .then((created) => {
                            vm.context = created
                            vm.contextId = created.contextId
                            vm.namespace = created.namespace
                            vm.description = created.description

                            vm.$store.commit('raiseNotification', {message: `Context '${vm.namespace}' created.`, type: 'success'})
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
