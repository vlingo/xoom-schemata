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
            items: [],
            open: [1, 2, 205],
            search: null,
            selected: [],
        }),
        computed: {
            filter() {
                return (item, search, textKey) => item[textKey].indexOf(search) > -1
            }
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
                                console.log(data);
                                for (let schema of data) {
                                    console.log(schema)
                                    vm.items.push({
                                        id: schema.id,
                                        name: schema.name,
                                        children: schema.units.map(u => {
                                            return {
                                                id: u.id,
                                                name: u.name,
                                                children: u.contexts.map(c => {
                                                    return {
                                                        id: c.id,
                                                        name: c.name,
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
            }
        }
    }
</script>

<style>

</style>
