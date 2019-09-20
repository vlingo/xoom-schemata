import Vue from 'vue'
import Vuex from 'vuex'
import { getField, updateField } from 'vuex-map-fields';

Vue.use(Vuex)

export default new Vuex.Store({
  strict: true,
  state: {
    error: undefined,
    schema: undefined,
    schemaVersion: undefined,
    selected: undefined,
    organization: undefined,
    unit: undefined,
    context: undefined,
    category: undefined,
    //schema: undefined,
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

    select (state, selected) {
      state.selected = selected
    }
  },
  actions: {

  },
  getters: {
    getField,
    unit: state => state.selected?.unitId ?? undefined,
    context: state => state.selected?.contextId ?? undefined,
    schema: state => state.selected?.schemaId ?? undefined,
  }
})
