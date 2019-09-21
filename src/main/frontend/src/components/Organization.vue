<template>
    <v-card class="xs12" height="95vh" width="100%">
        <v-card-title>Organization</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    lazy-validation
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

                                label="Name"
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
            <v-btn color="primary" @click="createOrganization"
                   :disabled="!name || !description">Create
            </v-btn>
            <v-btn color="secondary" to="/unit"
                   :disabled="!organizationId">Create Unit
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
                organizationId: '',
                name: '',
                description: '',
            }
        },
        computed: {
            ...mapFields([
                'organization',
            ]),
        },
        watch: {
            name() {
                this.organizationId = ''
            },
            description() {
                this.organizationId = ''
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
