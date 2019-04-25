<template>

<v-card min-height="30rem">
  <v-list dense>
    <v-list-tile
      ripple
      active-class="primary"
      v-for="version in versions" :key="version.id"
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
      if(this.schema)
        this.versions = this.schema.versions
    }
  },
  methods: {
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
