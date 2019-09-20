import Vue from 'vue'
import Vuex from 'vuex'
import { getField, updateField } from 'vuex-map-fields';

Vue.use(Vuex)

export default new Vuex.Store({
  strict: true,
  state: {
    error: undefined,
    schemaVersion: undefined,
    selected: undefined,
    organization: undefined,
    unit: undefined,
    context: undefined,
    category: undefined,
    schema: undefined,
    version: undefined
  },
  mutations: {
    updateField,
    raiseError (state, error) {
      state.error = error
    },

    dismissError (state) {
      state.error = undefined
    },

    selectSchema (state, selected) {
      state.schema = selected
    },

  },
  actions: {

  },
  getters: {
    getField,
    organizationId: state => state.selected?.organizationId ?? undefined,
    unitId: state => state.selected?.unitId ?? undefined,
    contextId: state => state.selected?.contextId ?? undefined,
    schemaId: state => state.selected?.schemaId ?? undefined,
  }
})
