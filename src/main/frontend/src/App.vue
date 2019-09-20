<template>
    <v-app>
        <v-system-bar height="5px" color="primary"></v-system-bar>
        <v-app-bar dense>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title class="headline">
                <span>vlingo</span>/<span class="font-weight-light">schemata</span>
            </v-toolbar-title>
            <div class="flex-grow-1"></div>
            <v-btn icon="">
                <v-icon>{{icons.sync}}</v-icon>
            </v-btn>
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
            <v-list dense shaped>
                <v-list-item v-for="(item,idx) in menu" :key="idx"
                             :to="item.route"
                             color="primary"
                >
                    <v-divider v-if="item.divider"></v-divider>

                    <v-list-item-action v-if="!item.divider">
                        <v-icon>{{item.icon}}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content v-if="!item.divider">
                        <v-list-item-title>
                            {{item.title}}
                        </v-list-item-title>
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
    import {mdiEllipseOutline, mdiFactory, mdiFileDocument, mdiHome, mdiStore, mdiSync, mdiTag} from '@mdi/js'

    export default {
        name: 'App',
        data() {
            return {
                drawer: true,
                menu: [
                    {route: '/schemata', title: 'Browse Schemata', icon: mdiHome},
                    {divider: true},
                    {route: '/404', title: 'Create Organization', icon: mdiFactory},
                    {route: '/404', title: 'Create Unit', icon: mdiStore},
                    {route: '/404', title: 'Create Context', icon: mdiEllipseOutline},
                    {route: '/404', title: 'Create Schema', icon: mdiFileDocument},
                    {route: '/404', title: 'Create Schema Version', icon: mdiTag},
                ],
                icons: {
                    sync: mdiSync
                }
            }
        }
    }
</script>
