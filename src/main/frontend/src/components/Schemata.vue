<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="45vh">
        <v-card-title>
            <v-text-field
                    v-model="search"
                    label="Search"
                    clearable
                    hide-details
                    clear-icon="close"
            ></v-text-field>
        </v-card-title>
        <v-card-text>
            <v-treeview
                    dense
                    :items="items"
                    :search="search"
                    :filter="filter"
                    :load-children="loadChildren"
                    return-object
                    activatable
                    transition
                    :active.sync="selected"
                    :open.sync="open"
                    @update:active="$emit('input', $event[0])"
            >
            </v-treeview>
        </v-card-text>
    </v-card>
</template>

<script>
    function ensureHttpOk(response) {
        if (!response.ok) {
            throw Error(`HTTP ${response.status}: ${response.statusText} (${response.url})`);
        }
        return response;
    }

    export default {
        data: () => ({
            items: [],
            search: null,
            open: [],
        }),
        computed: {
            filter() {
                return (item, search, textKey) => item[textKey].indexOf(search) > -1
            },
            selected: {
                get() {
                    return [this.$store.state.selected]
                },
                set(value) {
                    this.$store.commit('select', value[0])
                }
            }
        },
        created() {
            this.loadOrganizations()
        },
        methods: {
            loadOrganizations() {
                let vm = this
                fetch('/organizations')
                    .then(ensureHttpOk)
                    .then(response => response.json())
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
                return fetch(`/organizations/${org.id}/units`)
                    .then(ensureHttpOk)
                    .then(response => response.json())
                    .then(data => {
                        for (let unit of data) {
                            unit.id = `${org.id}-${unit.unitId}`
                            unit.type = 'unit'
                            unit.children = []
                            org.children.push(unit)
                        }
                    })


            },
            async loadContexts(unit) {
                return fetch(`/organizations/${unit.organizationId}/units/${unit.unitId}/contexts`)
                    .then(ensureHttpOk)
                    .then(response => response.json())
                    .then(data => {
                        for (let context of data) {
                            context.id = `${unit.id}-${context.contextId}`
                            context.name = context.namespace
                            context.type = 'context'
                            context.children = []
                            unit.children.push(context)
                        }
                    })
            }
            ,
            async loadSchemata(context) {
                return fetch(`/organizations/${context.organizationId}/units/${context.unitId}/contexts/${context.contextId}/schemas`)
                    .then(ensureHttpOk)
                    .then(response => response.json())
                    .then(data => {
                        for (let schema of data) {
                            schema.id = `${context.id}-${schema.schemaId}`
                            schema.type = 'schema'
                            context.children.push(schema)
                        }
                    })
            }
        }
    }
</script>

<style>

</style>
