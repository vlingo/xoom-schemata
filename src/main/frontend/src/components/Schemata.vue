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
            items: [],
            search: null,
            selected: [],
        }),
        computed: {
            filter() {
                return (item, search, textKey) => item[textKey].indexOf(search) > -1
            },
        },
        created() {
            this.loadSchemata()
        },
        methods: {
            loadSchemata() {
                let vm = this
                fetch('/api/organizations')
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }

                            response.json().then(function (data) {
                                for (let organization of data) {
                                    vm.items.push({
                                        id: organization.id,
                                        name: organization.name,
                                        type: 'organization',
                                        children: organization.units.map(u => {
                                            return {
                                                id: u.id,
                                                name: u.name,
                                                type: 'unit',
                                                children: u.contexts.map(c => {
                                                    return {
                                                        id: u.id + "-" + c.id,
                                                        name: c.name,
                                                        type: 'context',
                                                        contextId: c.id,
                                                        organizationId: organization.id,
                                                        unitId: u.id,
                                                        children: []
                                                    }
                                                })
                                            }
                                        }),
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
                if (item.type !== 'context')
                    return // only load additional items when a context is selected

                let vm = this
                return fetch(`/api/schemata/${item.organizationId}/${item.unitId}/${item.contextId}`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }

                            response.json().then(function (data) {
                                for (let schemaType of Object.keys(data)) {
                                    item.children.push({
                                        id: schemaType,
                                        name: schemaType,
                                        type: 'schemaType',
                                        children: data[schemaType].map(s => {
                                            return {
                                                id: s.id,
                                                name: s.id,
                                                type: 'schema',
                                                organizationId: item.organizationId,
                                                unitId:item.unitId,
                                                contextId: item.contextId,
                                                versions: s.versions
                                            }
                                        })
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
