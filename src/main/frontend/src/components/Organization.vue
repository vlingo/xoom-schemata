<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-organization">
        <v-card-title>Organization</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="organizationId"
                                label="OrganizationID"
                                disabled
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="name"
                                :rules="[rules.notEmpty]"
                                label="Name"
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
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="createOrganization"
                   :disabled="!valid">Create
            </v-btn>
            <v-btn color="secondary" to="/unit"
                   :disabled="!organizationId">Create Unit
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
                organizationId: '',
                name: '',
                description: '',
            }
        },

        methods: {
            createOrganization() {
                let vm = this
                Repository.createOrganization(this.name, this.description)
                    .then((created) => {
                            vm.organization = created
                            vm.organizationId = created.organizationId
                            vm.name = created.name
                            vm.description = created.description

                            vm.$store.commit('raiseNotification', {
                                message: `Organization '${vm.name}' created.`,
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
