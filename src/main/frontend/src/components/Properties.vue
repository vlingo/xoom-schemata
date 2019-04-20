<template>

<v-card min-height="30rem">
  <v-card-text>
  <v-tabs>
    <v-tab>Specification</v-tab>
    <v-tab>Status</v-tab>
    <v-tab>Description</v-tab>

    <!-- TODO: Factor out tab contents in separate components-->

    <v-tab-item>
      <code>{{ specification }}</code>
      <v-chip class="right">Specification for {{ schema }} <span v-if="version">v{{ version }}</span></v-chip>
    </v-tab-item>

    <v-tab-item>
      <v-list dense>
        <v-list-tile
          v-for="s in status" :key="s"
        >
          <v-list-tile-action>
            <v-icon :color="s === 'Published' ? 'primary' : ''">label</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ s }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
      <v-chip class="right">Status for {{ schema }}<span v-if="version">v{{ version }}</span></v-chip>
    </v-tab-item>

    <v-tab-item>
      <div v-html="compiledDescription()"></div>
      <v-chip class="right">Description for {{ schema }} <span v-if="version">v{{ version }}</span></v-chip>
    </v-tab-item>
  </v-tabs>
  </v-card-text>

</v-card>


</template>

<script>
import marked from 'marked'

export default {
  props: ['schema', 'version'],
  data: () => ({
    status: [],
    specification: '',
    description: ''
  }),
  watch: {
    schema: function () {
      this.fetchData()
    },
    version: function () {
      this.fetchData()
    },
  },
  methods: {
    fetchData() {
      this.status = []
      this.specification = ''
      this.description = ''
      // TODO: query an actual backend, this is just mock data
      if (this.schema === "com.google.search.adwords.events.AdWordsAudienceChosen" && this.version === "1.0.1") {
        this.status = ['Draft', 'Published']
        this.specification = `event AdWordsAccountCreated {
	type eventType
	version eventVersion
	timestamp ocurredOn
	string accountId
	string name
	data.com.google.search.adwords.data.AccountData:2.0.0
	int numberOfWords,
	data.com.google.search.adwords.data.PaymentMethod:1.3.2
}`
        this.description = '# Description\nAlso\n* supports\n* **Mark**_down_'
      } else if (this.schema === "com.google.search.adwords.events.AdWordsAudienceChosen" && this.version === "1.0.0") {
        this.status = ['Draft']
        this.specification = 'Bar'
        this.description = '**B**_a_z'
      }  else if (this.schema === "com.google.search.adwords.events.AdWordsAccountCreated" && this.version === "1.0.0") {
        this.status = ['Published']
        this.specification = 'Qux'
        this.description = '_Quux_'
      }  else if (this.schema === "com.google.search.adwords.events.AdWordsLocationCreated" && this.version === "1.0.0") {
        this.status = ['Published','Deprectated']
        this.specification = 'corge'
        this.description = '# grault'
      }
    },
    compiledDescription: function () {
      return marked(this.description, { sanitize: true })
    }
  }
}
</script>

<style>
.v-card {
  overflow-y: auto
}

code {
  width: 100%;
  font-size: larger;
  font-weight: lighter;
}
</style>
