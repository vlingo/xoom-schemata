<template>

    <v-card min-height="45vh">
        <v-list dense>
            <v-list-item
                    ripple
                    active-class="primary"
                    v-for="v in versions" :key="v.id"
                    @click="selectVersion(v)">
                <v-list-item-action>
                    <v-tooltip right>
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on" :color="version === v ? 'primary': ''">{{icon(v)}}
                            </v-icon>
                        </template>
                        <span>{{v.status}}</span>
                    </v-tooltip>
                </v-list-item-action>
                <v-list-item-content>
                    <v-list-item-title>{{ v.currentVersion }}</v-list-item-title>
                </v-list-item-content>
            </v-list-item>
        </v-list>
    </v-card>


</template>

<script>
    import {mdiDelete, mdiPencil, mdiTag} from '@mdi/js'
    import Repository from '@/api/SchemataRepository'
    import {mapFields} from 'vuex-map-fields';

    export default {
        data: () => ({
            versions: [],
        }),
        mounted() {
          this.loadVersions()
        },
        watch: {
            schema: function () {
                this.selectVersion(undefined)
                if (this.schema)
                    this.loadVersions()
            }
        },
        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),
        },
        methods: {
            selectVersion(selection) {
                this.selectedVersion = selection
                this.$emit('input', selection)
            },
            loadVersions() {
                console.log('load version')
                if (!(this.schema)) {
                    return
                }

                let vm = this
                Repository.getVersions(
                    this.schema.organizationId,
                    this.schema.unitId,
                    this.schema.contextId,
                    this.schema.schemaId)
                    .then(data => vm.versions = data)
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
