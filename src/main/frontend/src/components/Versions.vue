<template>

    <v-card  min-height="45vh">
        <v-list dense>
            <v-list-item
                    ripple
                    active-class="primary"
                    v-for="version in versions" :key="version.id"
                    @click="selectVersion(version)">
                <v-list-item-action>
                    <v-tooltip right>
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on" :color="selectedVersion === version ? 'primary': ''">{{icon(version)}}
                            </v-icon>
                        </template>
                        <span>{{version.status}}</span>
                    </v-tooltip>
                </v-list-item-action>
                <v-list-item-content>
                    <v-list-item-title>{{ version.currentVersion }}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-list>
    </v-card>


</template>

<script>
    import {mdiDelete, mdiPencil, mdiTag} from '@mdi/js'

    export default {
        props: ['schema'],
        data: () => ({
            versions: [],
            selectedVersion: undefined,
        }),
        watch: {
            schema: function () {
                this.selectVersion(undefined)
                if (this.schema)
                    this.loadVersions()
            }
        },
        computed: {},
        methods: {
            selectVersion(selection) {
                this.selectedVersion = selection
                this.$emit('input', selection)
            },
            loadVersions() {
                if (!(this.schema)) {
                    return
                }

                let vm = this
                let o = this.schema.organizationId
                let u = this.schema.unitId
                let c = this.schema.contextId
                let s = this.schema.schemaId
                fetch(`/organizations/${o}/units/${u}/contexts/${c}/schemas/${s}/versions`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }

                            response.json().then(function (data) {
                                vm.versions = data
                            });
                        }
                    )
                    .catch(function (err) {
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            },
            icon(version) {
                if (!version) return '';
                switch (version.status) {
                    case 'Published':
                        return mdiTag
                    case 'Draft':
                        return mdiPencil
                    case 'Removed':
                        return mdiDelete
                    default:
                        return 'insert_drive_file'
                }
            }
        }
    }
</script>

<style>
    .v-card {
        overflow-y: auto
    }
</style>
