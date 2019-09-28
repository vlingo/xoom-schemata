<template>
    <v-app>
        <v-app-bar dense style="z-index: 10" fixed>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer" id="schemata-navigation-toggle"></v-app-bar-nav-icon>
            <v-toolbar-title class="headline">
                <span>vlingo</span>/<span class="font-weight-light">schemata</span>
            </v-toolbar-title>
            <div class="flex-grow-1"></div>

        </v-app-bar>
        <v-system-bar height="5px" color="primary" style="z-index: 10" fixed></v-system-bar>

        <v-navigation-drawer
                app
                class='pt-12'
                :expand-on-hover="drawer"
                disable-resize-watcher
                hide-overlay
                permanent
        >
            <v-list dense shaped class="mt-1">
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

        <v-alert
                v-if="$store.state.error"
                class="mt-5 ml-2 mr-2 mb-0 pt-0 pb-0 error elevation-15"
                style="z-index: 10"
        >
            <v-row align="center">
                <v-col class="grow">{{$store.state.error.message}}</v-col>
                <v-col class="shrink">
                    <v-btn icon dark @click="$store.commit('dismissError')">
                        <v-icon>{{icons.close}}</v-icon>
                    </v-btn>
                </v-col>
            </v-row>
        </v-alert>
        <v-content>

            <v-container
                    class="grid-list-md fill-height"
                    :class="$store.state.error ? 'mt-0' : 'mt-8'"
                    fluid
            >
                <router-view/>
            </v-container>
        </v-content>
    </v-app>
</template>

<script>
    import {
        mdiCloseCircleOutline,
        mdiEllipseOutline,
        mdiFactory,
        mdiFileDocument,
        mdiHome,
        mdiPencil,
        mdiStore,
        mdiSync,
        mdiTag
    } from '@mdi/js'

    export default {
        name: 'App',
        data() {
            return {
                drawer: true,
                menu: [
                    {route: '/schemata', title: 'Browse Schemata', icon: mdiHome},
                    {route: '/editor', title: 'Edit Schema Version', icon: mdiPencil},
                    {divider: true},
                    {route: '/organization', title: 'New Organization', icon: mdiFactory},
                    {route: '/unit', title: 'New Unit', icon: mdiStore},
                    {route: '/context', title: 'New Context', icon: mdiEllipseOutline},
                    {route: '/schema', title: 'New Schema', icon: mdiFileDocument},
                    {route: '/schemaVersion', title: 'New Schema Version', icon: mdiTag},
                ],
                icons: {
                    sync: mdiSync,
                    close: mdiCloseCircleOutline
                }
            }
        }
    }
</script>
