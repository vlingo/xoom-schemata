import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    error: undefined,
    schema: undefined,
    schemaVersion: undefined,
    selected: undefined,
    organizations:[]
  },
  mutations: {
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
    organization: state => state.selected?.organizationId ?? undefined,
    unit: state => state.selected?.unitId ?? undefined,
    context: state => state.selected?.contextId ?? undefined,
    schema: state => state.selected?.schemaId ?? undefined,
  }
})
