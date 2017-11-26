import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

import * as state from './state'
import * as getters from './getters'
import * as mutations from './mutations'
import * as actions from './actions'
import kitchen from './modules/kitchen'
import bedroom from './modules/bedroom'
import bathroom from './modules/bathroom'

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  modules: {
    kitchen,
    bedroom,
    bathroom
  }
})
