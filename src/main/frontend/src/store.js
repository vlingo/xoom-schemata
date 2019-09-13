import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    schema: undefined,
    schemaVersion: undefined,
    selected: undefined,
  },
  mutations: {
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
