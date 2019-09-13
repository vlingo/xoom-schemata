<template>
  <v-app>
    <v-app-bar dense>
      <v-toolbar-title class="headline text-uppercase">
        <span>Vlingo</span><span class="font-weight-light">Schemata</span>
      </v-toolbar-title>
    </v-app-bar>

    <v-content>
      <router-view/>
    </v-content>
  </v-app>
</template>

<script>

export default {
  name: 'App',
  data () {
    return {
      //
    }
  }
}
</script>
