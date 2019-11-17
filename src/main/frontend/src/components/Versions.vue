<template>

    <v-card height="45vh" id="schemata-versions">
        <v-list dense>
            <v-list-item-group v-model="selected" color="primary">
                <v-list-item v-for="v in schemaVersions" :key="v.id" ripple>
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
    import {mdiDelete, mdiLabel, mdiLabelOff, mdiPencil} from '@mdi/js'
    import {mapFields} from 'vuex-map-fields';

    export default {
        data() {
            return {
                selected: undefined
            }
        },
        watch: {
            schema: function () {
                this.schemaVersion = undefined
            },
            selected: function (value) {
                if (value === undefined) {
                    this.schemaVersion = undefined
                } else {
                    this.schemaVersion = this.schemaVersions[value]
                }
            }
        },
        computed: {
            ...mapFields([
                'schema',
                'schemaVersions',
                'schemaVersion',
            ]),
        },
        methods: {
            icon(version) {
                if (!version) return '';
                switch (version.status) {
                    case 'Published':
                        return mdiLabel
                    case 'Draft':
                        return mdiPencil
                    case 'Deprecated':
                        return mdiLabelOff
                    case 'Removed':
                        return mdiDelete
                    default:
                        return ''
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
