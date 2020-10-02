<template>
        <v-row>
        <v-col cols="12" class="pt-0">
            <Schemata @vs-error="onError"/>
        </v-col>

        <v-col cols="12" md="3" class="pt-0">
            <Versions @vs-error="onError"/>
        </v-col>
        <v-col cols="12" md="9" class="pt-0">
            <Properties @vs-error="onError"/>
        </v-col>
        </v-row>
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
            error: false,
        }),
        computed: {
            hasError: {
                get: function () {
                    return this.error !== false
                },
                set: function (value) {
                    // only used to reset the error state when alert is dismissed
                    this.error = value
                }
            }
        },
        methods: {
            async onError(event) {
                this.error = await event.message
            }
        }
    }
</script>
