<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-unit">
        <v-card-title>Unit</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="unitId"
                                label="UnitID"
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
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="name"
                                label="Name"
                                :rules="[rules.notEmpty]"
                                required
                        ></v-text-field>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-textarea
                                v-model="description"
                                label="Description"
                                :rules="[rules.notEmpty]"
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
            <v-btn color="secondary" to="/context"
                   :disabled="!unitId">Create Context
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
                unitId: '',
                name: '',
                description: '',
            }
        },

        methods: {
            createUnit() {
                let vm = this
                Repository.createUnit(
                    this.organization.organizationId,
                    this.name,
                    this.description)
                    .then((created) => {
                            vm.unit = created
                            vm.unitId = created.unitId
                            vm.name = created.name
                            vm.description = created.description

                            vm.$store.commit('raiseNotification', {message: `Unit '${vm.name}' created.`, type: 'success'})
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
