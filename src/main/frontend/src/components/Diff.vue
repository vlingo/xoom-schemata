<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="95vh" min-width="100%">
        <v-card-text>
            <v-dialog v-model="show">
                <v-card>
                    <editor
                            id="diff-editor"
                            :diffEditor="true"
                            :original="original"
                            v-model="patched"
                            theme="vs-dark"
                            language="javascript"
                            height="400"
                            :options="{ readOnly: true, automaticLayout: true }"
                    ></editor>
                </v-card>
            </v-dialog>
        </v-card-text>
    </v-card>
</template>

<script>
    import editor from 'monaco-editor-vue';

    export default {
        components: {editor},
        props: {
            changes: {
                type: Array,
                required: true,
            },
            original: {
                type: String,
                required: false,
                default: ""
            },
            patched: {
                type: String,
                required: false,
                default: ""
            },
            show: {
                required: true,
                default: false
            }
        },
        data: () => {
            return {
            }
        },

        mounted() {

        }
    }
</script>
