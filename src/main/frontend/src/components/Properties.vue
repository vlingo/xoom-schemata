<template>
    <v-card height="45vh" id="schemata-properties">
        <v-card-text>
            <v-alert v-if="status && status !== 'Published'" :value="true" type="warning" outlined>
                Status <b>{{status}}</b>. Do not use in production.
            </v-alert>
            <v-tabs>
                <v-tab>Specification</v-tab>
                <v-tab>Description</v-tab>

                <v-tab-item>
                    <code>{{ specification }}</code>
                </v-tab-item>

                <v-tab-item>
                    <div v-html="compiledDescription()"></div>
                </v-tab-item>
            </v-tabs>
        </v-card-text>
        <v-card-actions>
            <v-btn outlined color="primary" @click="publish"
                   :disabled="status !== 'Draft'">
                <v-icon>{{icons.publish}}</v-icon>
                Publish
            </v-btn>
            <v-btn outlined color="warning" @click="deprecate"
                   :disabled="status !== 'Published' && status !== 'Draft'">
                <v-icon>{{icons.deprecate}}</v-icon>
                Deprecate
            </v-btn>
            <v-btn outlined color="error" @click="remove"
                   :disabled="status !== 'Deprecated' && status !== 'Draft'">
                <v-icon>{{icons.delete}}</v-icon>
                Remove
            </v-btn>
        </v-card-actions>
    </v-card>


</template>

<script>
    import {mapFields} from 'vuex-map-fields';
    import {mdiDelete, mdiLabel, mdiLabelOff} from '@mdi/js'
    import marked from 'marked'
    import Repository from '@/api/SchemataRepository'


    export default {
        data: function () {
            return {
                icons: {
                    publish: mdiLabel,
                    delete: mdiDelete,
                    deprecate: mdiLabelOff
                }
            }
        },
        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),

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
        methods: {
            compiledDescription: function () {
                return marked(this.description)
            },

            publish() {
                this._setStatus('Published')
            },
            deprecate() {
                this._setStatus('Deprecated')
            },
            remove() {
                this._setStatus('Removed')
            },

            _setStatus(status) {
                let vm = this
                Repository.setSchemaVersionStatus(
                    this.version.organizationId,
                    this.version.unitId,
                    this.version.contextId,
                    this.version.schemaId,
                    this.version.schemaVersionId,
                    status
                )
                    .then((response) => {

                            vm.$store.commit('raiseNotification', {
                                message: `Status for ${vm.schema.name} v${vm.version.currentVersion} set to ${status}.`,
                                type: 'success'
                            })
                            vm.$store.commit('selectSchemaVersion', response.data)
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
    .v-card {
        overflow-y: auto
    }

    code {
        width: 100%;
        font-size: larger;
        font-weight: lighter;
    }
</style>
