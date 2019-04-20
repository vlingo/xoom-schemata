<template>

<v-card min-height="30rem">
  <v-list dense>
    <v-list-tile
      ripple
      active-class="primary"
      v-for="version in versions" :key="version"
      @click="selectVersion(version)">
      <v-list-tile-action>
        <v-icon :color="selectedVersion === version ? 'primary': ''">insert_drive_file</v-icon>
      </v-list-tile-action>
      <v-list-tile-content>
        <v-list-tile-title>{{ version }}</v-list-tile-title>
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
      this.fetchVersions()
    }
  },
  methods: {
    fetchVersions() {
      // TODO: query an actual backend, this is just mock data
      switch (this.schema) {
        case "com.google.search.adwords.events.AdWordsAudienceChosen":
          this.versions = ["1.0.0", "1.0.1", "1.0.2", "1.2.0", "2.0.0"]
          break
        case "com.google.search.adwords.events.AdWordsAccountCreated":
          this.versions = ["1.0.0", "1.0.1"]
          break
        case "com.google.search.adwords.events.AdWordsLocationCreated":
          this.versions = ["1.0.0", "1.0.1", "1.0.2", "1.0.3", "2.0.0", "2.0.1"]
          break
        default:
          this.versions = []
      }
    },
    selectVersion(selection) {
      this.selectedVersion = selection
      this.$emit('input', selection)
    }
  }
}
</script>

<style>
.v-card {
  overflow-y: auto
}
</style>
