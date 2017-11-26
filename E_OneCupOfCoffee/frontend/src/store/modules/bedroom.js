import axios from 'axios'

const state = {
  alarm: null,
  airCondition: null,
  motion: null,
  temperature: null,
  light: null,
  bed: null
}

const getters = {
  bedroomState: state => {
    return state
  },
  alarmClock: state => {
    return state.alarm
  },
  airCondition: state => {
    return state.airCondition
  },
  motion: state => {
    return state.motion
  },
  temperature: state => {
    return state.temperature
  },
  light: state => {
    return state.light
  },
  bed: state => {
    return state.bed
  }
}

const mutations = {
  setAlarmClock: (state, payload) => {
    state.alarm = payload
  },
  setAirCondition: (state, payload) => {
    state.airCondition = payload
  },
  setMotion: (state, payload) => {
    state.motion = payload
  },
  setTemperature: (state, payload) => {
    state.temperature = payload
  },
  setLight: (state, payload) => {
    state.light = payload
  },
  setBed: (state, payload) => {
    state.bed = payload
  }
}

const actions = {
  bedroomGetFromApi: ({commit}) => {
    axios.get('bedroom').then(response => {
      commit('setAlarmClock', response.data.alarm)
      commit('setAirCondition', response.data.airCondition)
      commit('setMotion', response.data.motion)
      commit('setTemperature', response.data.temperature)
      commit('setLight', response.data.light)
      commit('setBed', response.data.bed)
    }).catch(error => {
      console.log('Napaka v bedroomGetFromApi:')
      console.log(error)
    })
  },
  bedroomPostToApi: function () {
    axios.post('bedroom', this.getters.bedroomState ).then(response => {
      console.log('bedroomPostToApi OK')
    }).catch(error => {
      console.log('Napaka v bedroomPostToApi:')
      console.log(error)
    })
  },
  setAlarmClock: ({commit}, payload) => {
    commit('setAlarmClock', payload)
  }
}

export default {
  state,
  getters,
  mutations,
  actions
}
