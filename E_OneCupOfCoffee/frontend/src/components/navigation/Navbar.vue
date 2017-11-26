<template>
    <b-navbar toggleable="md" type="dark" variant="info">

        <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>

        <b-navbar-brand href="#">1 cup of #coffee</b-navbar-brand>

        <b-collapse is-nav id="nav_collapse">

            <b-navbar-nav>
                <b-nav-item :to="{ name: 'kitchen' }">Kuhinja</b-nav-item>
                <b-nav-item :to="{ name: 'bathroom' }">Kopalnica</b-nav-item>
                <b-nav-item :to="{ name: 'bedroom' }">Spalnica</b-nav-item>
            </b-navbar-nav>
        </b-collapse>
    </b-navbar>
</template>
<script>
  export default {
    name: 'bh-navbar'
  }
</script>
<style>
</style>
