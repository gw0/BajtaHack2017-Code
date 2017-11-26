<template>
    <b-container>
        <b-row v-if="heater === null">
            <b-col offset-sm="3" sm="6">Podatki niso na voljo. Preverite povezavo s strežnikom.</b-col>
        </b-row>
        <b-row v-if="heater !== null">
            <b-col offset-sm="3" sm="6"> <p :class="heater ? 'green' : 'red'"><b>Ogrevanje:</b> {{ heaterStatus }} </p></b-col>
            <b-col offset-sm="3" sm="6"> <p :class="motion ? 'green' : 'red'"><b>Gibanje:</b> {{ motionStatus }} </p></b-col>
            <b-col offset-sm="3" sm="6"> <p :class="light ? 'green' : 'red'"><b>Luči:</b> {{ lightStatus }} </p></b-col>
        </b-row>
    </b-container>
</template>
<script>
  export default {
    data () {
      return {
        timer: ''
      }
    },
    computed: {
      heater: function () {
        return this.$store.getters.bathroomHeater
      },
      motion: function () {
        return this.$store.getters.bathroomMotion
      },
      light: function () {
        return this.$store.getters.bathroomLight
      },
      heaterStatus: function () {
        return this.heater ? 'vključeno' : 'izključeno'
      },
      motionStatus: function () {
        return this.motion ? 'zaznano' : 'ni zaznano'
      },
      lightStatus: function () {
        return this.light ? 'prižgana' : 'ugasnjena'
      },
    },
    created () {
      this.timer = setInterval(() => {
        this.$store.dispatch('bathroomGetFromApi')
      },1000)
    },
    destroyed() {
      clearInterval(this.timer)
    }
  }
</script>
<style>
</style>
