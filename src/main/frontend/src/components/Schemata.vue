<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card height="45vh" id="schemata-treeview">
        <v-card-title>
            <v-text-field
                    v-model="search"
                    label="Search"
                    clearable
                    hide-details
                    :clear-icon="icons.clear"
            ></v-text-field>
        </v-card-title>
        <v-card-text>
            <v-treeview
                    dense
                    :items="items"
                    :search="search"
                    :filter="filter"
                    :load-children="loadChildren"
                    open-on-click
                    return-object
                    activatable
                    transition
                    :active.sync="schema"
                    :open.sync="open"
            >
                <template v-slot:prepend="{ item }">
                    <v-tooltip right v-if="item.category  && icons.categories[item.category]">
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on">
                                {{icons.categories[item.category]}}
                            </v-icon>

                        </template>
                        <span>{{item.category}}</span>
                    </v-tooltip>


                </template>
            </v-treeview>
        </v-card-text>
    </v-card>
</template>

<script>
    import Repository from '@/api/SchemataRepository'
    import {
        mdiClose,
        mdiCogs,
        mdiDatabase,
        mdiEmailOpen,
        mdiFileDocument,
        mdiHelpCircle,
        mdiPlaylistPlay
    } from '@mdi/js'

    export default {
        data: () => ({
            items: [],
            search: null,
            open: [],
            icons: {
                close: mdiClose,
                categories: {
                    Command: mdiCogs,
                    Data: mdiDatabase,
                    Document: mdiFileDocument,
                    Envelope: mdiEmailOpen,
                    Event: mdiPlaylistPlay,
                    Unknown: mdiHelpCircle
                }
            }

        }),
        computed: {
            filter() {
                return (item, search, textKey) => item[textKey].indexOf(search) > -1
            },
            schema: {
                get() {
                    return [this.$store.schema]
                },
                set(value) {
                    this.$store.commit('selectSchema', value[0])
                }
            },

        },
        created() {
            this.loadOrganizations()
        },
        methods: {
            loadOrganizations() {
                let vm = this
                Repository.getOrganizations()
                    .then(data => {
                        for (let organization of data) {
                            organization.id = organization.organizationId
                            organization.type = 'organization'
                            organization.children = []
                            vm.items.push(organization)
                        }
                    })
            },

            async loadChildren(item) {
                let vm = this
                let loadPromise = undefined
                switch (item.type) {
                    case 'organization':
                        loadPromise = this.loadUnits(item)
                        break
                    case 'unit':
                        loadPromise = this.loadContexts(item)
                        break
                    case 'context':
                        loadPromise = this.loadSchemata(item)
                        break
                    default:
                        loadPromise = new Promise(() => {
                            throw Error(`Unknown type ${item.type} requested. Current selections: ${JSON.stringify(item)}.`)
                        })
                }
                loadPromise.catch(function (err) {
                    vm.$store.commit('raiseError', {message: err})
                });
                return loadPromise
            },

            async loadUnits(org) {
                return Repository.getUnits(org.id)
                    .then(data => {
                        for (let unit of data) {
                            unit.id = unit.unitId
                            unit.type = 'unit'
                            unit.children = []
                            org.children.push(unit)
                        }
                    })


            },
            async loadContexts(unit) {
                return Repository.getContexts(unit.organizationId, unit.unitId)
                    .then(data => {
                        for (let context of data) {
                            context.id = context.contextId
                            context.name = context.namespace
                            context.type = 'context'
                            context.children = []
                            unit.children.push(context)
                        }
                    })
            }
            ,
            async loadSchemata(context) {
                return Repository.getSchemata(context.organizationId, context.unitId, context.contextId)
                    .then(data => {
                        for (let schema of data) {
                            schema.id = schema.schemaId
                            schema.type = 'schema'
                            context.children.push(schema)
                        }
                    })
            }
        }
    }
</script>

<style>
    .v-card {
        overflow-y: auto
    }
</style>
