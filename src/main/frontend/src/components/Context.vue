<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-context">
        <v-card-title>Context</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
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
                                :rules="[rules.notEmpty]"
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
                                :rules="[rules.notEmpty]"
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
                                :rules="[rules.notEmpty]"
                                label="Namespace"
                                required
                        ></v-text-field>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-textarea
                                v-model="description"
                                :rules="[rules.notEmpty]"
                                label="Description"
                                required
                        ></v-textarea>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-btn color="info" @click="clearForm">New</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   :disabled="!valid"
                   @click="createUnit">Create
            </v-btn>
            <v-btn color="secondary" to="/schema"
                   :disabled="!contextId">Create Schema
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import Repository from '@/api/SchemataRepository'
    import selectboxLoaders from '@/mixins/selectbox-loaders'
    import validationRules from '@/mixins/form-validation-rules'

    export default {
        mixins: [selectboxLoaders, validationRules],


        data: () => {
            return {
                contextId: '',
                namespace: '',
                description: '',
            }
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

                            vm.$store.commit('raiseNotification', {
                                message: `Context '${vm.namespace}' created.`,
                                type: 'success'
                            })
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
