<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="95vh" min-width="100%">
        <v-card-title>Edit</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
                    lazy-validation
            >
                <v-row>
                <v-col class="d-flex" cols="12" sm="6">
                <v-select
                        :items="organizations"
                        label="Organization"
                ></v-select>
                </v-col>
                <v-col class="d-flex" cols="12" sm="6">
                <v-select
                        :items="units"
                        label="Unit"
                ></v-select>
                </v-col>
                <v-col class="d-flex" cols="12" sm="6">
                <v-select
                        :items="contexts"
                        label="Context"
                ></v-select>
                </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-select
                                :items="categories"
                                label="Category"
                        ></v-select>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>

    export default {
        computed: {
            organizations() {
                return []
            },
            units() {
                return []
            },
            contexts() {
                return []
            },
            categories() {
                return []
            },
        },
        methods: {
        }
    }
</script>
