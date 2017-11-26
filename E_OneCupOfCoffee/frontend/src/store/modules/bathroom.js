import axios from 'axios'

const state = {
  heater: null,
  light: null,
  motion: null
}

const getters = {
  bathroomMotion: state => {
    return state.motion
  },
  bathroomLight: state => {
    return state.light
  },
  bathroomHeater: state => {
    return state.heater
  }
}

const mutations = {
  setMotion: (state, payload) => {
    state.motion = payload
  },
  setHeater: (state, payload) => {
    state.heater = payload
  },
  setLight: (state, payload) => {
    state.light = payload
  }
}

const actions = {
  bathroomGetFromApi: ({commit}) => {
    axios.get('bathroom').then(response => {
      commit('setHeater', response.data.heater)
      commit('setMotion', response.data.motion)
      commit('setLight', response.data.light)
    }).catch(error => {
      console.log('Napaka v bathroomGetFromApi:')
      console.log(error)
    })
  }
}

export default {
  state,
  getters,
  mutations,
  actions
}
