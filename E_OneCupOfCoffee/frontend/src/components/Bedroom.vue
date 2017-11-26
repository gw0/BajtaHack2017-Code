<template>
    <b-container>
        <b-row v-if="alarmClock === null">
            <b-col offset-sm="3" sm="6">Podatki niso na voljo. Preverite povezavo s strežnikom.</b-col>
        </b-row>
        <b-row>
            <b-col sm="6" offset-sm="3" v-if="alarmClock"><h1 class="top">Budilka</h1>
                <b-form-input v-model.lazy="alarmClock"
                              type="time"></b-form-input>
            </b-col>
            <b-col sm="6" offset-sm="3" v-if="bed"><h1 class="top">Stanje</h1> V postelji.
            </b-col>
        </b-row>
    </b-container>
</template>
<script>
  export default {
    data () {
      return {
        title: 'bedroom'
      }
    },
    computed: {
      alarmClock: {
        // getter
        get: function () {
          return this.$store.getters.alarmClock
        },
        // setter
        set: function (newAlarmValue) {
          this.$store.dispatch('setAlarmClock', newAlarmValue)
          this.$store.dispatch('bedroomPostToApi')
        }
      },
      alarm: {
        // getter
        get: function () {
          return this.$store.getters.alarm
        },
        // setter
        set: function (newAlarmValue) {
          this.$store.dispatch('setAlarm', newAlarmValue)
        }
      },
      airCondition: function () {
        return this.$store.getters.airCondition
      },
      bed: function () {
        return this.$store.getters.bed
      }
    },
    created () {
      this.$store.dispatch('bedroomGetFromApi')
    }
  }
</script>
<style>
    h1.top {
        margin-top: 10px;
        text-align: center;
    }
</style>
