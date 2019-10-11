<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-schema-version">
        <v-card-title>Schema Version</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
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
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="contexts"
                                label="Context"
                                :rules="[rules.notEmpty]"
                                :loading="loading.contexts"
                                return-object
                                item-value="contextId"
                                item-text="namespace"
                                v-model="context"
                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="schemata"
                                label="Schema"
                                :loading="loading.schema"
                                return-object
                                :rules="[rules.notEmpty]"
                                item-value="schemaId"
                                item-text="name"
                                v-model="schema"
                        ></v-autocomplete>
                    </v-col>


                    <v-col class="d-flex" cols="4">
                        <v-text-field
                                v-model="previousVersion"
                                :rules="[rules.notEmpty,rules.versionNumber]"
                                label="Previous Version"
                                required
                        ></v-text-field>
                    </v-col>

                    <v-col class="d-flex" cols="4">
                        <v-text-field
                                v-model="currentVersion"
                                :rules="[rules.notEmpty,rules.versionNumber]"
                                label="Current Version"
                                required
                        ></v-text-field>
                    </v-col>

                    <v-col class="d-flex" cols="4">
                        <v-autocomplete
                                :items="statuses"
                                :rules="[rules.notEmpty]"
                                label="Status"
                                v-model="status"
                        ></v-autocomplete>
                    </v-col>

                    <v-col cols="12">
                        <label class="v-label theme--light"
                               :class="{'primary--text':descriptionEditorActive}">Description</label>
                        <editor
                                id="description-editor"
                                @change="activateDescriptionEditor"
                                v-model="description"
                                theme="vs-dark"
                                language="markdown"
                                height="200"
                                :options="editorOptions"
                        ></editor>
                    </v-col>
                    <v-col cols="12">
                        <label class="v-label theme--light"
                               :class="{'primary--text':specificationEditorActive}"
                        >Specification</label>
                        <editor
                                id="specification-editor"
                                v-model="specification"
                                @change="activateSpecificationEditor"
                                theme="vs-dark"
                                language="javascript"
                                height="300"
                                :options="editorOptions"
                        ></editor>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   :disabled="!valid || !description || !specification"
                   @click="createSchemaVersion">Create
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import selectboxLoaders from '@/mixins/selectbox-loaders'
    import Repository from '@/api/SchemataRepository'
    import editor from 'monaco-editor-vue';
    import validationRules from '@/mixins/form-validation-rules'

    export default {
        mixins: [selectboxLoaders, validationRules],

        components: {editor},
        data: () => {
            return {
                schemaVersionId: '',
                description: '',
                specification: '',
                status: 'Draft',
                previousVersion: '',
                currentVersion: '',
                statuses: ['Draft', 'Published', 'Deprecated', 'Removed'],

                descriptionEditorActive: false,
                specificationEditorActive: false,

                editorOptions: {
                    automaticLayout: true,
                }
            }
        },

        methods: {
            createSchemaVersion() {
                let vm = this
                Repository.createSchemaVersion(
                    this.organization.organizationId,
                    this.unit.unitId,
                    this.context.contextId,
                    this.schema.schemaId,
                    this.specification,
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

                            vm.$store.commit('raiseNotification', {
                                message: `Schema v${vm.currentVersion} created.`,
                                type: 'success'
                            })
                        }
                    )
                    .catch(function (err) {
                        let response = err.response ? err.response.data + ' - ' : ''
                        vm.$store.commit('raiseError', {message: response + err})
                    })
            },
            activateDescriptionEditor() {
                this.descriptionEditorActive = true
                this.specificationEditorActive = false
            },
            activateSpecificationEditor() {
                this.descriptionEditorActive = false
                this.specificationEditorActive = true
            }
        }
    }
</script>
