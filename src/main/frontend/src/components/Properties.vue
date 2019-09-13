<template>

    <v-card min-height="30rem">
        <v-card-title v-if="version && version.status && version.status !== 'Published'"
        class="ma-0 pa-0" primary-title>
            <v-alert v-if="version.status !== 'Published'" :value="true" type="warning" outlined>
                Status <b>{{version.status}}</b>. Do not use in production.
            </v-alert>
        </v-card-title>
        <v-card-text class="pt-0">
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
    import marked from 'marked'

    export default {
        props: ['schema', 'version'],
        data: () => ({
            specification: '',
            description: ''
        }),
        watch: {
            schema: function () {
                this.fetchData()
            },
            version: function () {
                this.fetchData()
            },
        },
        methods: {
            fetchData() {
                if (!(this.schema && this.version)) {
                    return
                }

                let vm = this
                fetch(`/api/schemata/${this.schema.organizationId}/${this.schema.unitId}/${this.schema.contextId}/${this.schema.id}/${this.version.id}`)
                    .then(
                        function (response) {
                            if (response.status !== 200) {
                                vm.$emit('vs-error', {status: response.status, message: response.text()})
                                return;
                            }

                            response.json().then(function (data) {
                                vm.specification = data.specification
                                vm.description = data.description
                            });
                        }
                    )
                    .catch(function (err) {
                        vm.$emit('vs-error', {status: 0, message: err})
                    });
            },
            compiledDescription: function () {
                return marked(this.description, {sanitize: true})
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
