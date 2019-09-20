<template>

    <v-card height="45vh">
        <v-list dense>
            <v-list-item-group v-model="selected" color="primary">
                <v-list-item v-for="v in versions" :key="v.id" ripple>
                    <v-list-item-action>
                        <v-tooltip right>
                            <template v-slot:activator="{ on }">
                                <v-icon v-on="on">
                                    {{icon(v)}}
                                </v-icon>
                            </template>
                            <span>{{v.status}}</span>
                        </v-tooltip>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>{{ v.currentVersion }}</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list-item-group>
        </v-list>
    </v-card>


</template>

<script>
    import {mdiDelete, mdiPencil, mdiTag} from '@mdi/js'
    import Repository from '@/api/SchemataRepository'
    import {mapFields} from 'vuex-map-fields';

    export default {
        data() {
            return {
                selected: undefined
            }
        },
        watch: {
            schema: function () {
                this.version = undefined
            },
            selected: function (value) {
                if (value === undefined) {
                    this.version = undefined
                } else {
                    this.version = this.versions[value]
                }
            }
        },
        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),
        },
        asyncComputed: {
            async versions() {
                if (!(this.schema)) {
                    return
                }

                let vm = this
                return await Repository.getVersions(
                    this.schema.organizationId,
                    this.schema.unitId,
                    this.schema.contextId,
                    this.schema.schemaId)
                    .catch(function (err) {
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            }
        },
        methods: {
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

</style>
