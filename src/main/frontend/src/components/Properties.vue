<template>
    <v-card height="45vh" id="schemata-properties">
        <v-card-text>
            <v-alert v-if="status && status !== 'Published'" :value="true" type="warning" outlined>
                Status <b>{{status}}</b>. Do not use in production.
            </v-alert>
            <v-tabs>
                <v-tab>Specification</v-tab>
                <v-tab>Description</v-tab>

                <v-tab-item>
                    <code>{{ specification }}</code>
                </v-tab-item>

                <v-tab-item>
                    <div v-html="compiledDescription()"></div>
                </v-tab-item>
            </v-tabs>
        </v-card-text>

    </v-card>


</template>

<script>
    import {mapFields} from 'vuex-map-fields';
    import marked from 'marked'

    export default {
        computed: {
            ...mapFields([
                'schema',
                'version'
            ]),

            specification() {
                return this.version?.specification ?? ''
            },
            description() {
                return this.version?.description ?? ''
            },
            status() {
                return this.version?.status ?? ''
            },
        },
        methods: {
            compiledDescription: function () {
                return marked(this.description)
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
