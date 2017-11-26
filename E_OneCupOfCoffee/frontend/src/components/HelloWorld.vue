<template>
  <div class="hello">
      <b-container>
          <div v-if="!msg">No data yet.</div>
          {{ msg }}
      </b-container>
  </div>
</template>

<script>
  import axios from 'axios'
  export default {
    name: 'HelloWorld',
    data () {
      return {
        msg: ''
      }
    },
    created () {
      axios.get('user').then(response => {
        this.msg = response.data
      }).catch(error => {
        console.log('napaka')
        console.log(error)
      })
    }
  }
</script>

<style>
</style>
