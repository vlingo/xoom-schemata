import Vue from 'vue'
import Vuex from 'vuex'
import {getField, updateField} from 'vuex-map-fields';
import Repository from "@/api/SchemataRepository";

Vue.use(Vuex)

export default new Vuex.Store({
  strict: true,
  state: {
    loading: false,
    error: undefined,
    notification: undefined,
    schemaVersion: undefined,
    selected: undefined,
    organization: undefined,
    unit: undefined,
    context: undefined,
    category: undefined,
    schema: undefined,
    schemaVersions: undefined,
  },
  mutations: {
    updateField,
    startLoading(state) {
      state.loading = true
    },
    finishLoading(state) {
      state.loading = false
    },
    raiseError(state, error) {
      state.error = error
    },

    dismissError(state) {
      state.error = undefined
    },

    raiseNotification(state, notification) {
      notification.type = notification.type || 'info'
      state.notification = notification
    },

    dismissNotification(state) {
      state.notification = undefined
    },

    selectOrganization(state, selected) {
      state.organization = selected
    },
    selectUnit(state, selected) {
      state.unit = selected
    },
    selectContext(state, selected) {
      state.context = selected
    },
    updateSchema(state, selected) {
      state.schema = selected
    },
    updateSchemaVersion(state, selected) {
      state.schemaVersion = selected
    },
    updateSchemaVersions(state, versions) {
      state.schemaVersions = versions
    }

  },
  actions: {
    selectSchema(context, selected) {
      this.commit('updateSchema', selected)
      this.dispatch('loadVersions')
    },
    selectSchemaVersion(context, selected) {
      context.commit('updateSchemaVersion', selected)
    },
    loadVersions(context) {
      if (!(context.state.schema)) {
        return
      }

      Repository.getVersions(
        context.state.schema.organizationId,
        context.state.schema.unitId,
        context.state.schema.contextId,
        context.state.schema.schemaId)
        .then(response => context.commit('updateSchemaVersions', response))
        .catch(err => context.commit('raiseError', {status: 0, message: err}));
    }
  },
  getters: {
    getField,
    organizationId: state => state.selected?.organizationId ?? undefined,
    unitId: state => state.selected?.unitId ?? undefined,
    contextId: state => state.selected?.contextId ?? undefined,
    schemaId: state => state.selected?.schemaId ?? undefined,
  }
})
