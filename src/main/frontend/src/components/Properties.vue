<template>
    <v-card height="48vh" id="schemata-properties">
        <v-card-text v-if="version">
            <v-alert v-if="status && status !== 'Published'" :value="true" type="warning" dense outlined>
                Status <b>{{status}}</b>. Do not use in production.
            </v-alert>
            <v-tabs>
                <v-tab>Specification</v-tab>
                <v-tab>Description</v-tab>

                <v-tab-item>
                    <editor
                            id="specification-editor"
                            v-model="currentSpecification"
                            theme="vs-dark"
                            language="javascript"
                            height="200"
                            :options="editorOptions"
                    ></editor>
                    <br>
                    <v-btn color="info"
                           :disabled="readOnly"
                           @click="saveSpecification">Save
                    </v-btn>
                </v-tab-item>

                <v-tab-item>
                    <div v-if="readOnly" v-html="compiledDescription()"></div>
                    <editor v-if="!readOnly"
                            id="description-editor"
                            v-model="currentDescription"
                            theme="vs-dark"
                            language="markdown"
                            height="200"
                            :options="editorOptions"
                    ></editor>
                    <v-dialog v-model="previewDialog">
                        <v-card>
                            <v-card-title class="headline">{{schema.name}} v{{version.currentVersion}}</v-card-title>
                            <v-divider></v-divider>
                            <br>
                            <v-card-text v-html="compiledDescription()"></v-card-text>
                        </v-card>
                    </v-dialog>
                    <br>
                    <v-btn color="secondary"
                           :disabled="readOnly"
                           @click="previewDialog = true">Preview
                    </v-btn>&nbsp;
                    <v-btn color="info"
                           :disabled="readOnly"
                           @click="saveDescription">Save
                    </v-btn>
                </v-tab-item>
            </v-tabs>
        </v-card-text>
    </v-card>


</template>

<script>
    import {mapFields} from 'vuex-map-fields';
    import marked from 'marked'
    import editor from 'monaco-editor-vue';
    import Repository from '@/api/SchemataRepository'

    export default {
        components: {editor},
        data: function () {
            return {
                currentSpecification: undefined,
                currentDescription: undefined,
                previewDialog: false,
            }
        },

        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),

            editorOptions() {
                return {
                    readOnly: this.readOnly,
                    automaticLayout: true,
                }
            },

            specification() {
                return this.version?.specification ?? ''
            },
            description() {
                return this.version?.description ?? ''
            },
            status() {
                return this.version?.status ?? ''
            },
            readOnly() {
                return this.status !== 'Draft'
            }
        },
        watch: {
            specification(val) {
                this.currentSpecification = val
            },
            description(val) {
                this.currentDescription = val
            }
        },
        methods: {
            compiledDescription: function () {
                return marked(this.currentDescription ?? '')
            },
            saveSpecification: function () {
                let vm = this
                Repository.saveSchemaVersionSpecification(
                    this.version.organizationId,
                    this.version.unitId,
                    this.version.contextId,
                    this.version.schemaId,
                    this.version.schemaVersionId,
                    this.currentSpecification
                )
                    .then(() => {
                            vm.$store.commit('raiseNotification', {
                                message: `Specification for ${vm.schema.name} v${vm.version.currentVersion} updated.`,
                                type: 'success'
                            })
                        }
                    )
                    .catch(function (err) {
                        let response = err.response ? err.response.data + ' - ' : ''
                        vm.$store.commit('raiseError', {message: response + err})
                    })
            },
            saveDescription: function () {
                let vm = this
                Repository.saveSchemaVersionDescription(
                    this.version.organizationId,
                    this.version.unitId,
                    this.version.contextId,
                    this.version.schemaId,
                    this.version.schemaVersionId,
                    this.currentDescription
                )
                    .then(() => {
                            vm.$store.commit('raiseNotification', {
                                message: `Description for ${vm.schema.name} v${vm.version.currentVersion} updated.`,
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

<style>
</style>
