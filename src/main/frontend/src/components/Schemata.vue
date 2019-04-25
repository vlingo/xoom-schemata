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
                                console.log('Looks like there was a problem. Status Code: ' +
                                    response.status);
                                console.log(response);
                                return;
                            }

                            response.json().then(function (data) {
                                for (let item of data) {
                                    console.log(item)
                                    vm.items.push({
                                        id: item.id,
                                        name: item.name,
                                        type: 'organization',
                                        children: item.units.map(u => {
                                            return {
                                                id: u.id,
                                                name: u.name,
                                                type: 'unit',
                                                children: u.contexts.map(c => {
                                                    return {
                                                        id: u.id + "-" + c.id,
                                                        name: c.name,
                                                        type: 'context',
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
                        console.log('Error: ', err);
                    });
            },
            async loadChildren(item) {
                if (item.type !== 'context')
                    return // only load additional items when a context is selected

                let vm = this
                return fetch('/api/organizations/o/u/c') // FIXME: use parents in tree to construct path
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                console.log('Looks like there was a problem. Status Code: ' +
                                    response.status);
                                console.log(response);
                                return;
                            }

                            response.json().then(function (data) {
                                console.log(data)
                                for (let schemaType of Object.keys(data)) {
                                    console.log(schemaType)
                                    item.children.push({
                                        id: schemaType,
                                        name: schemaType,
                                        type: 'schemaType',
                                        children: data[schemaType].map(s => {
                                            return {
                                                id: s.id,
                                                name: s.id,
                                                type: 'schema',
                                                versions: s.versions
                                            }
                                        })
                                    })
                                }
                            });
                        }
                    )
                    .catch(function (err) {
                        console.log('Error: ', err);
                    });
            }
        }
    }
</script>

<style>

</style>
