<template>
<v-container class="fluid grid-list-md">
  <v-flex class="xs12">
  <Schemata v-model="schema"/>
  </v-flex>

  <v-layout row wrap>
  <v-flex class="xs12 md3">
  <Versions :schema="schema" v-model="version"/>
  </v-flex>
    <v-flex class="xs12 md9">
    <Properties :schema="schema" :version="version"/>
    </v-flex>
  </v-layout>
</v-container>
</template>

<script>
import Schemata from '../components/Schemata'
import Versions from '../components/Versions'
import Properties from '../components/Properties'

export default {
  components: {
    Schemata,
    Versions,
    Properties
  },
  data: () => ({
    schema: [],
    version: undefined,
  })

}
</script>
