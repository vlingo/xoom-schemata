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
                                v-model="organizationId"
                                @input="selection => $store.dispatch('select',selection)"
                                :disabled="schemaVersionId !== undefined"
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
                                v-model="unitId"
                                @input="selection => $store.dispatch('select',selection)"
                                :disabled="schemaVersionId !== undefined"
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
                                v-model="contextId"
                                @input="selection => $store.dispatch('select',selection)"
                                :disabled="schemaVersionId !== undefined"
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
                                v-model="schemaId"
                                @input="selection => $store.dispatch('select',selection)"
                                :disabled="schemaVersionId !== undefined"
                        ></v-autocomplete>
                    </v-col>


                    <v-col class="d-flex" cols="6">
                        <v-text-field
                                v-model="previousVersion"
                                :rules="[rules.notEmpty,rules.versionNumber]"
                                label="Previous Version"
                                required
                                :disabled="schemaVersionId !== undefined"
                        ></v-text-field>
                    </v-col>

                    <v-col class="d-flex" cols="6">
                        <v-text-field
                                v-model="currentVersion"
                                :rules="[rules.notEmpty,rules.versionNumber]"
                                label="Current Version"
                                required
                                :disabled="schemaVersionId !== undefined"
                        ></v-text-field>
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
            <v-btn color="info" @click="clearForm">New</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary"
                   :disabled="!valid || !description || !specification"
                   @click="create">Create
            </v-btn>
        </v-card-actions>

        <diff
                :original="diffOriginalSpecification"
                :patched="diffPatchedSpecification"
                :show="diffShow"
                :changes="diffChanges"
                @close="resetDiff()"
        ></diff>
    </v-card>
</template>

<script>
    import selectboxLoaders from '@/mixins/selectbox-loaders'
    import Repository from '@/api/SchemataRepository'
    import editor from 'monaco-editor-vue';
    import validationRules from '@/mixins/form-validation-rules'
    import diff from '@/components/Diff';

    export default {
        mixins: [selectboxLoaders, validationRules],

        components: {editor, diff},
        data: () => {
            return {
                organizationId: undefined,
                unitId: undefined,
                contextId: undefined,
                schemaId: undefined,
                schemaVersionId: undefined,
                description: '',
                specification: '',
                previousVersion: '',
                currentVersion: '',

                descriptionEditorActive: false,
                specificationEditorActive: false,

                diffShow: false,
                diffOriginalSpecification: undefined,
                diffPatchedSpecification: undefined,
                diffChanges: [],
            }
        },
        computed: {
            editorOptions() {
                return {
                    automaticLayout: true,
                    readOnly: this.schemaVersionId !== undefined
                }
            }
        },
        methods: {
            create() {
                let vm = this
                Repository.createSchemaVersion(
                    this.$store.getters.organizationId,
                    this.$store.getters.unitId,
                    this.$store.getters.contextId,
                    this.$store.getters.schemaId,
                    this.specification,
                    this.description,
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
                            vm.$store.dispatch('loadVersions')
                        }
                    )
                    .catch(function (err) {
                        if (err.response && err.response.status === 409) {
                            let result = JSON.parse(err.response.data);
                            vm.diffShow = true;
                            vm.diffOriginalSpecification = result.oldSpecification;
                            vm.diffPatchedSpecification = result.newSpecification;
                            vm.diffChanges = result.changes;

                            // eslint-disable-next-line
                            console.log(err.response)
                        }
                        vm.$store.commit('raiseError', {message: 'Incompatible changes within a compatible version change'})

                    })
            },
            activateDescriptionEditor() {
                this.descriptionEditorActive = true
                this.specificationEditorActive = false
            },
            activateSpecificationEditor() {
                this.descriptionEditorActive = false
                this.specificationEditorActive = true
            },
            load(organizationId, unitId, contextId, schemaId, schemaVersionId) {
                let vm = this
                Repository.getVersion(organizationId, unitId, contextId, schemaId, schemaVersionId)
                    .then((loaded) => {
                        vm.organizationId = loaded.organizationId
                        vm.unitId = loaded.unitId
                        vm.contextId = loaded.contextId
                        vm.speficication = loaded.speficication
                        vm.description = loaded.description
                        vm.status = loaded.status
                        vm.previousVersion = loaded.previousVersion
                        vm.currentVersion = loaded.currentVersion
                    })
            },
            resetDiff() {
                this.diffShow = false
                this.diffOriginalSpecification = undefined
                this.diffPatchedSpecification = undefined
                this.diffChanges = []
                this.$store.commit('dismissError');
                this.$store.commit('finishLoading');
            }
        },
        mounted() {
            this.organizationId = this.$store.getters.organizationId
            this.unitId = this.$store.getters.unitId
            this.contextId = this.$store.getters.contextId
            this.schemaId = this.$store.getters.schemaId
            let schemaVersionId = this.$store.getters.schemaVersionId
            if (this.organizationId && this.unitId && this.contextId && this.schemaId && schemaVersionId) {
                this.load(this.organizationId, this.unitId, this.contextId, this.schemaId, schemaVersionId)
            }
        }
    }
</script>
