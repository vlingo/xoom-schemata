<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
    <v-card class="xs12" min-height="95vh" min-width="100%">
        <v-card-text>
            <v-dialog v-model="open">
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
                    <v-list disabled dense>
                        <v-list-item v-for="(c,i) in changes" :key="i">
                            <v-list-item-icon>
                                <v-icon>{{iconFor(c.subject)}}</v-icon>
                                <v-icon :color="colorFor(c.type)">{{iconFor(c.type)}}</v-icon>
                            </v-list-item-icon>
                            <v-list-item-content>
                                <v-list-item-title>
                                    <span class="old">{{c.oldValue}}</span>
                                    <span v-if="c.oldValue"><v-icon>{{icons.arrowRight}}</v-icon></span>
                                    <span class="new">{{c.newValue}}</span>
                                </v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list>
                </v-card>
            </v-dialog>
        </v-card-text>
    </v-card>
</template>

<script>
    import editor from 'monaco-editor-vue';
    import {
        mdiArrowRight,
        mdiCodeBraces,
        mdiLabelOff,
        mdiMinusBox,
        mdiNotEqual,
        mdiPlusBox,
        mdiSwapVertical,
        mdiVariable
    } from '@mdi/js'


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
                icons: {
                    CHANGE_FIELD: mdiVariable,
                    CHANGE_FIELD_VERSION: mdiLabelOff,
                    CHANGE_FIELD_TYPE: mdiNotEqual,
                    CHANGE_FIELD_DEFAULT: mdiNotEqual,
                    ADD_FIELD: mdiPlusBox,
                    CHANGE_TYPE: mdiCodeBraces,
                    REMOVE_FIELD: mdiMinusBox,
                    MOVE_FIELD: mdiSwapVertical,
                    arrowRight: mdiArrowRight,
                }
            }
        },
        computed: {
            open: {
                get() {
                    return this.show
                },
                set(newVal) {
                    if (!newVal) this.$emit('close')
                }
            }
        },
        watch: {
            show() {
                this.open = this.show
            }
        },

        methods: {
            iconFor(type) {
                return this.icons[type]
            },
            colorFor(type) {
                switch (type) {
                    case "CHANGE_FIELD":
                    case "CHANGE_FIELD_VERSION":
                    case "CHANGE_FIELD_TYPE":
                    case "CHANGE_FIELD_DEFAULT":
                    case "MOVE_FIELD":
                        return 'warning';
                    case "ADD_FIELD":
                        return 'primary';
                    case "CHANGE_TYPE":
                    case "REMOVE_FIELD":
                        return 'error';
                }
            }
        },

        mounted() {

        }
    }
</script>
<style>
    .old {
        text-decoration: line-through;
        color: grey;
    }
</style>
