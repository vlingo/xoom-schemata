<template>

    <v-card height="46vh" id="schemata-versions">
        <v-list dense>
            <v-list-item-group v-model="selectedVersion" color="primary">
                <v-list-item v-for="v in versions" :key="v.schemaVersionId" :value="v" ripple>
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

    export default {
      data: function() {
        return {
          selectedVersion: undefined
        }
      },
      watch: {
        selectedVersion(newSelection) {
          this.$store.dispatch('select', newSelection)
        },
      },
      computed: {
        versions() { return this.$store.getters.schemaVersions }
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
