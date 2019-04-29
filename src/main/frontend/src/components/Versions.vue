<template>

    <v-card min-height="30rem">
        <v-list dense>
            <v-list-tile
                    ripple
                    active-class="primary"
                    v-for="version in versions" :key="version.id"
                    @click="selectVersion(version)">
                <v-list-tile-action>
                    <v-tooltip right>
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on" :color="selectedVersion === version ? 'primary': ''">{{icon(version)}}</v-icon>
                        </template>
                        <span>{{version.status}}</span>
                    </v-tooltip>
                </v-list-tile-action>
                <v-list-tile-content>
                    <v-list-tile-title>{{ version.id }}</v-list-tile-title>
                </v-list-tile-content>
            </v-list-tile>
        </v-list>
    </v-card>


</template>

<script>
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
                    this.versions = this.schema.versions
            }
        },
        methods: {
            selectVersion(selection) {
                this.selectedVersion = selection
                this.$emit('input', selection)
            },
            icon(version) {
                if (!version) return '';
                switch (version.status) {
                    case 'Published':
                        return 'label'
                    case 'Draft':
                        return 'create'
                    case 'Removed':
                        return 'delete'
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
