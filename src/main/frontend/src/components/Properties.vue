<template>
    <v-card height="45vh" id="schemata-properties">
        <v-card-text>
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
                           :disabled="status !== 'Draft'"
                           @click="saveSpecification">Save
                    </v-btn>
                </v-tab-item>

                <v-tab-item>
                    <div v-html="compiledDescription()"></div>
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
            }
        },

        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),

            editorOptions() {
                return {
                    readOnly: this.status !== 'Draft',
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
        },
        watch: {
            specification(val) {
                this.currentSpecification = val
            }
        },
        methods: {
            compiledDescription: function () {
                return marked(this.description)
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
            }
        }
    }
</script>

<style>
</style>
