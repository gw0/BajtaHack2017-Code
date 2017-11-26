<template>
    <div id="app">
        <bh-navbar></bh-navbar>
        <router-view/>
    </div>
</template>

<script>
  import BhNavbar from './components/navigation/Navbar.vue'
  export default {
    name: 'app',
    components: {
      BhNavbar
    }
  }
</script>

<style>
    .green {
        color: green;
    }
    .red {
        color: red;
    }
    .container {
        margin-top: 50px;
    }
    .row {
        text-align: center;
    }
</style>
