import Vue from 'vue'
import Vuex from 'vuex'
import {getField, updateField} from 'vuex-map-fields';
import Repository from "@/api/SchemataRepository";

Vue.use(Vuex)

export default new Vuex.Store({
  strict: false, //FIXME re-enable after refactoring
  state: {
    loading: false,
    error: undefined,
    notification: undefined,
    selection: undefined,
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

    updateSelection(state, selection) {
      state.selection = selection
    },

    updateSchemaVersions(state, versions) {
      state.schemaVersions = versions
    }

  },
  actions: {
    select(context, selection) {
      if(!selection) return

      context.commit('updateSelection', selection)
      if(selection.type === 'schema') {
        context.dispatch('loadVersions')
      }
      if(selection.type !== 'schema' &&  !selection.schemaVersionId) {
        context.commit('updateSchemaVersions', [])
      }
    },

    deselect(context) {
      context.commit('updateSelection', undefined)
    },


    loadVersions(context) {
      if (!(context.state.selection?.type === 'schema')) {
        return
      }

      Repository.getVersions(
        context.state.selection.organizationId,
        context.state.selection.unitId,
        context.state.selection.contextId,
        context.state.selection.schemaId)
        .then(response => {
          context.commit('updateSchemaVersions', response)
        })
        .catch(err => context.commit('raiseError', {status: 0, message: err}));
    }
  },
  getters: {
    getField,
    organizationId: state => state.selection?.organizationId ?? undefined,
    unitId: state => state.selection?.unitId ?? undefined,
    contextId: state => state.selection?.contextId ?? undefined,
    schemaId: state => state.selection?.schemaId ?? undefined,
    schemaVersionId: state => state.selection?.schemaVersionId ?? undefined,
    schemaVersions: state => state.schemaVersions ?? [],
    schemaVersion: state => state.selection?.schemaVersionId ? state.selection : undefined
  }
})
