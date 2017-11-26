import axios from 'axios'

const state = {
  coffee: null,
  oven: null,
  temperature: null,
  water: null
}

const getters = {
  kitchenCoffee: state => {
    return state.coffee
  },
  kitchenOven: state => {
    return state.oven
  },
  kitchenOvenTemp: state => {
    return state.temperature
  },
  kitchenWater: state => {
    return state.water
  }
}

const mutations = {
  setCoffee: (state, payload) => {
    state.coffee = payload
  },
  setOven: (state, payload) => {
    state.oven = payload
  },
  setOvenTemp: (state, payload) => {
    state.temperature = payload
  },
  setWater: (state, payload) => {
    state.water = !payload
  }
}

const actions = {
  kitchenGetFromApi: ({commit}) => {
    axios.get('kitchen').then(response => {
      commit('setOven', response.data.oven)
      commit('setOvenTemp', response.data.temperature)
      commit('setWater', response.data.water)
      commit('setCoffee', response.data.coffee)
    }).catch(error => {
      console.log('Napaka v kitchenGetFromApi:')
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
