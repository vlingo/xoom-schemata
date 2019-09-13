<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12"  min-height="45vh">
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
                get() { return [this.$store.state.selected]},
                set(value) { this.$store.commit('select', value[0])}
            }
        },
        created() {
            this.loadOrganizations()
        },
        methods: {
            loadOrganizations() {
                let vm = this
                fetch('/organizations')
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                            }
                            response.json().then(function (data) {
                                for (let organization of data) {
                                    organization.id = organization.organizationId
                                    organization.type = 'organization'
                                    organization.children = []
                                    vm.items.push(organization)
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            },

            async loadChildren(item) {
                switch (item.type) {
                    case 'organization':
                        return this.loadUnits(item)
                    case 'unit':
                        return this.loadContexts(item)
                    case 'context':
                        return this.loadSchemata(item)
                    default:
                        this.$emit('vs-error', {
                            status: 0,
                            message: `Unknown type ${item.type} requested. Current selections: ${item}.`
                        })
                }
            },

            async loadUnits(org) {
                let vm = this
                return fetch(`/organizations/${org.id}/units`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }
                            response.json().then(function (data) {
                                for (let unit of data) {
                                    unit.id = `${org.id}-${unit.unitId}`
                                    unit.type = 'unit'
                                    unit.children = []
                                    org.children.push(unit)
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        // TODO factor out error handling
                        vm.$emit('vs-error', {status: 0, message: err})
                    });

            },
            async loadContexts(unit) {
                let vm = this
                return fetch(`/organizations/${unit.organizationId}/units/${unit.unitId}/contexts`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }
                            response.json().then(function (data) {
                                for (let context of data) {
                                    context.id = `${unit.id}-${context.contextId}`
                                    context.name = context.namespace
                                    context.type = 'context'
                                    context.children = []
                                    unit.children.push(context)
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        // TODO factor out error handling
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            },
            async loadSchemata(context) {
                let vm = this
                return fetch(`/organizations/${context.organizationId}/units/${context.unitId}/contexts/${context.contextId}/schemas`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }
                            response.json().then(function (data) {
                                for (let schema of data) {
                                    schema.id = `${context.id}-${schema.schemaId}`
                                    schema.type = 'schema'

                                    context.children.push(schema)
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        // TODO factor out error handling
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            }
        }
    }
</script>

<style>

</style>
