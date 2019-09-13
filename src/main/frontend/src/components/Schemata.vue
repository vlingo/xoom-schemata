<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12">
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
                    open-on-click
                    @update:active="$emit('input', $event[0])"
            >
                <template v-slot:prepend="{ item }">
                    <v-icon v-if="item.children">folder</v-icon>
                </template>
            </v-treeview>
        </v-card-text>
    </v-card>
</template>

<script>
    export default {
        data: () => ({
            items: [],
            search: null,
            selected: [],
            open: [],
        }),
        computed: {
            filter() {
                return (item, search, textKey) => item[textKey].indexOf(search) > -1
            },
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
                                    vm.items.push({
                                        id: organization.organizationId,
                                        name: organization.name,
                                        type: 'organization',
                                        children: []
                                    })
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
                                    org.children.push({
                                        id: `${org.id}-${unit.unitId}`,
                                        organizationId: org.id,
                                        unitId: unit.unitId,
                                        name: unit.name,
                                        type: 'unit',
                                        children: []
                                    })
                                }
                            });
                        }
                    )
                    .catch(function (err) {
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
                                    unit.children.push({
                                        id: `${unit.id}-${context.contextId}`,
                                        organizationId: context.organizationId,
                                        unitId: context.unitId,
                                        contextId: context.contextId,
                                        name: context.namespace,
                                        type: 'context',
                                        children: []
                                    })
                                }
                            });
                        }
                    )
                    .catch(function (err) {
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
                                    context.children.push({
                                        id: `${context.id}-${schema.schemaId}`,
                                        organizationId: schema.organizationId,
                                        unitId: schema.unitId,
                                        contextId: schema.contextId,
                                        name: schema.name,
                                        category: schema.category,
                                        type: 'context',
                                        children: []
                                    })
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            }
        }
    }
</script>

<style>

</style>
