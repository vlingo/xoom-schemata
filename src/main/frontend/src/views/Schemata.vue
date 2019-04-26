<template>
    <v-container class="fluid grid-list-md">
        <v-alert v-model="hasError" dismissible type="error">{{error}}</v-alert>
        <v-flex class="xs12">
            <Schemata v-model="schema" v-on:vs-error="onError"/>
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
            error: null,
        }),
        computed: {
            hasError: {
                get: function () {
                    return this.error !== null
                },
                set: function (value) {
                    // only used to reset the error state when alert is dismissed
                    this.error = null
                }
            }
        },
        methods: {
            async onError(event) {
                console.log(event)
                this.error = await event.message + ` (HTTP ${event.status})`
            }
        }
    }
</script>
