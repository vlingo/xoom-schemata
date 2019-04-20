<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-card class="xs12">
    <v-card-title>
    <v-text-field
      v-model="search"
      label="Search"
      clearable
      clear-icon="close"
    ></v-text-field>
    </v-card-title>
  <v-card-text>
    <v-treeview
      :items="items"
      :search="search"
      :filter="filter"
      :open.sync="open"
      activatable
      :active.sync="selected"
      @update:active="$emit('input', $event[0])"
    >
      <template v-slot:prepend="{ item }">
      <v-icon
        v-if="item.children"
        v-text="`md-${item.id === 1 ? 'home' : 'folder'}`"
      ></v-icon>
      </template>
    </v-treeview>
  </v-card-text>
  </v-card>
</template>

<script>
export default {
  data: () => ({
    items: [
      {
        id: 1,
        name: 'Google Search',
        children: [
          {
            id: 2,
            name: 'AdWords',
            children: [
              {
                id: 201,
                name: 'Commands'
              },
              {
                id: 202,
                name: 'Data'
              },
              {
                id: 203,
                name: 'Documents'
              },
              {
                id: 204,
                name: 'Envelopes'
              },
              {
                id: 205,
                name: 'Events',
                children: [
                  {
                    id: 'com.google.search.adwords.events.AdWordsAccountCreated',
                    name: 'com.google.search.adwords.events.AdWordsAccountCreated'
                  },
                  {
                    id: 'com.google.search.adwords.events.AdWordsAudienceChosen',
                    name: 'com.google.search.adwords.events.AdWordsAudienceChosen'
                  },
                  {
                    id: 'com.google.search.adwords.events.AdWordsLocationCreated',
                    name: 'com.google.search.adwords.events.AdWordsLocationCreated'
                  },
                ]
              }
            ]
          },
          {
            id: 3,
            name: 'Google Fiber',
            children: []
          },
          {
            id: 4,
            name: 'GV',
            children: []
          },
          {
            id: 5,
            name: 'Jigsaw',
            children: []
          }
        ]
      }
    ],
    open: [1, 2, 205],
    search: null,
    selected: [],
  }),
  computed: {
    filter() {
      return (item, search, textKey) => item[textKey].indexOf(search) > -1
    }
  }
}
</script>

<style>

</style>
