<template>
    <v-app>
        <v-system-bar height="5px" color="primary"></v-system-bar>
        <v-app-bar dense>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title class="headline">
                <span>vlingo</span>/<span class="font-weight-light">schemata</span>
            </v-toolbar-title>
        </v-app-bar>

        <v-navigation-drawer
                app
                class='mt-12'
                style='top: 6px'
                :expand-on-hover="drawer"
                disable-resize-watcher
                hide-overlay
                permanent
        >
            <v-list dense>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>home</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Browse Schemata</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-divider></v-divider>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>business</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Create Organization</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>store</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Create Unit</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>folder</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Create Context</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>description</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Create Schema</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
                <v-list-item>
                    <v-list-item-action>
                        <v-icon>label</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Create Schema Version</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>

        <v-content>
            <v-alert
                    v-if="$store.state.error"
                    class="mt-2 ml-2 mr-2 mb-0 pt-0 pb-0 error"
            >
                <v-row align="center">
                    <v-col class="grow">{{$store.state.error.message}}</v-col>
                    <v-col class="shrink">
                        <v-btn icon dark @click="$store.commit('dismissError')">
                            <v-icon>highlight_off</v-icon>
                        </v-btn>
                    </v-col>
                </v-row>
            </v-alert>
            <v-container
                    class="grid-list-md fill-height"
                    fluid
            >
                <router-view/>
            </v-container>
        </v-content>
    </v-app>
</template>

<script>

    export default {
        name: 'App',
        data() {
            return {
                drawer: true,
            }
        }
    }
</script>
