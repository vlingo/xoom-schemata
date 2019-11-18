<template>
<v-card height="45vh" id="schemata-properties">

  <v-tabs>
    <v-tab>Specification</v-tab>
    <v-tab>Description</v-tab>
    <v-spacer></v-spacer>
    <v-chip label color="warning" v-if="status && status !== 'Published'" class="mt-3 mr-3">
      Status&nbsp;<b>{{status}}</b>. Do not use in production.
    </v-chip>


    <v-tab-item>
      <v-card-text>
        <editor
          id="specification-editor"
          v-model="currentSpecification"
          theme="vs-dark"
          language="javascript"
          height="200"
          :options="editorOptions"
        ></editor>
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
        <v-spacer></v-spacer>
        <v-btn outlined color="info"
               :disabled="!schemaVersion"
               @click="loadSources">
          <v-icon>{{icons.source}}</v-icon>
          Source
        </v-btn>
        <v-btn color="info"
               :disabled="status !== 'Draft'"
               @click="saveSpecification">Save
        </v-btn>
      </v-card-actions>
      <v-dialog v-model="sourceDialog">
        <v-card>
          <editor
            id="source-editor"
            v-model="sources.java"
            theme="vs-dark"
            language="java"
            height="400"
            :options="{ readOnly: true, automaticLayout: true }"
          ></editor>
        </v-card>
      </v-dialog>
    </v-tab-item>

    <v-tab-item>
      <v-card-text>
        <div v-html="compiledDescription()"></div>
      </v-card-text>
    </v-tab-item>
  </v-tabs>

</v-card>

</template>

<script>
import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull} from '@mdi/js'
import marked from 'marked'
import Repository from '@/api/SchemataRepository'
import editor from 'monaco-editor-vue';

export default {
  components: {editor},
  data: function () {
    return {
      currentSpecification: undefined,
      icons: {
        publish: mdiLabel,
        delete: mdiDelete,
        deprecate: mdiLabelOff,
        source: mdiSourcePull
      },
      sourceDialog: false,
      sources: {
        java: undefined,
      }
    }
  },
  computed: {
    editorOptions() {
      return {
        readOnly: this.status !== 'Draft',
        automaticLayout: true,
      }
    },

    schemaVersion() {
      return this.$store.getters.schemaVersion
    },

    specification() {
      return this.schemaVersion?.specification ?? ''
    },
    description() {
      return this.schemaVersion?.description ?? ''
    },
    status() {
      return this.schemaVersion?.status ?? ''
    },
  },
  watch: {
    specification(val) {
      this.currentSpecification = val
    }
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
        this.schemaVersion.organizationId,
        this.schemaVersion.unitId,
        this.schemaVersion.contextId,
        this.schemaVersion.schemaId,
        this.schemaVersion.schemaVersionId,
        status)
      .then(response => vm.$store.dispatch('select', response))
      .then(() => vm.$store.dispatch('loadVersions'))
      .then(() => {
          vm.$store.commit('raiseNotification', {
            message: `Status for v${vm.schemaVersion.currentVersion} updated.`,
            type: 'success'
          })
        }
      )
      .catch(function (err) {
        let response = err.response ? err.response.data + ' - ' : ''
        vm.$store.commit('raiseError', {message: response + err})
      })
    },

    saveSpecification: function () {
      let vm = this
      Repository.saveSchemaVersionSpecification(
        this.schemaVersion.organizationId,
        this.schemaVersion.unitId,
        this.schemaVersion.contextId,
        this.schemaVersion.schemaId,
        this.schemaVersion.schemaVersionId,
        this.currentSpecification
      )
      .then(response => vm.$store.dispatch('select', response))
      .then(() => vm.$store.dispatch('loadVersions'))
      .then(() => {
          vm.$store.commit('raiseNotification', {
            message: `Specification v${vm.schemaVersion.currentVersion} updated.`,
            type: 'success'
          })
        }
      )
      .catch(function (err) {
        let response = err.response ? err.response.data + ' - ' : ''
        vm.$store.commit('raiseError', {message: response + err})
      })
    },
    loadSources() {
      let vm = this
      Repository.loadSources(
        this.schemaVersion.organizationId,
        this.schemaVersion.unitId,
        this.schemaVersion.contextId,
        this.schemaVersion.schemaId,
        this.schemaVersion.schemaVersionId,
        "java"
      )
      .then(response => {
        vm.sources.java = response
        vm.sourceDialog = true
      })
      .catch(function (err) {
        let response = err.response ? err.response.data + ' - ' : ''
        vm.$store.commit('raiseError', {message: response + err})
      })
    }
  }
}
</script>

<style>

</style>
