<template>
    <b-container>
        <b-row v-if="oven === null">
            <b-col offset-sm="3" sm="6">Podatki niso na voljo. Preverite povezavo s strežnikom.</b-col>
        </b-row>
        <b-row v-if="oven !== null">
            <b-col offset-sm="3" sm="6"> <p :class="oven ? 'green' : 'red'"><b>Pečica:</b> {{ ovenStatus }}</p></b-col>
            <b-col offset-sm="3" sm="6"> <p :class="(temperature <= 50) && (temperature >= 40) ? 'green' : 'red'"><b>Temperatura v pečici:</b> {{ temperature }} °C</p></b-col>
            <b-col offset-sm="3" sm="6"> <p :class="coffee ? 'green' : 'red'"><b>Kuhalnik kave:</b> {{ coffeeStatus }} </p></b-col>
            <b-col offset-sm="3" sm="6"> <p :class="water ? 'green' : 'red'"><b>Nivo vode:</b> {{ waterStatus }} </p></b-col>
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
      oven: function () {
        return this.$store.getters.kitchenOven
      },
      temperature: function () {
        return this.$store.getters.kitchenOvenTemp.toFixed(2)
      },
      water: function () {
        return this.$store.getters.kitchenWater
      },
      coffee: function () {
        return this.$store.getters.kitchenCoffee
      },
      waterStatus: function () {
        return this.heater ? 'zadosten' : 'ni zadosten'
      },
      coffeeStatus: function () {
        return this.coffee ? 'vključen' : 'izključen'
      },
      ovenStatus: function () {
        return this.oven ? 'prižgana' : 'ugasnjena'
      },
    },
    created () {
      this.timer = setInterval(() => {
        this.$store.dispatch('kitchenGetFromApi')
      },1000)
    },
    destroyed() {
      clearInterval(this.timer)
    }
  }
</script>
<style>
</style>
