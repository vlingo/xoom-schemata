<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="95vh" min-width="100%">
        <v-card-title>Edit</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
                    lazy-validation
            >
                <v-select
                        :items="items"
                        label="Organization"
                ></v-select>
                <v-select
                        :items="items"
                        label="Unit"
                ></v-select>
                <v-select
                        :items="items"
                        label="Context"
                ></v-select>

            </v-form>
        </v-card-text>
    </v-card>
</template>
