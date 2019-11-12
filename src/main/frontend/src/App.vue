<template>
    <v-app>
        <v-app-bar dense style="z-index: 10" fixed>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer" id="schemata-navigation-toggle"></v-app-bar-nav-icon>
            <v-toolbar-title class="headline">
                <span>vlingo</span>/<span class="font-weight-light">schemata</span>
            </v-toolbar-title>
            <div class="flex-grow-1"></div>

        </v-app-bar>
        <v-progress-linear height="5px" :indeterminate="$store.state.loading" :value="$store.state.loading ? false : 100" style="z-index: 10" fixed></v-progress-linear>

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

        <v-snackbar
                v-if="$store.state.error"
                v-model="showError"
                :timeout="0"
                top
                color="error"
        >
            {{$store.state.error.message}}
            <v-btn icon dark @click="showError = false">
                <v-icon>{{icons.close}}</v-icon>
            </v-btn>
        </v-snackbar>
        <v-snackbar
                v-if="$store.state.notification"
                v-model="showNotification"
                :timeout="3000"
                top
                :color="$store.state.notification.type"
        >
            {{$store.state.notification.message}}
            <v-btn icon dark @click="showNotification = false">
                <v-icon>{{icons.close}}</v-icon>
            </v-btn>
        </v-snackbar>
        <v-content>

            <v-container
                    class="grid-list-md fill-height mt-8"
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
    import { mapFields } from 'vuex-map-fields';

    export default {
        name: 'App',
        data() {
            return {
                drawer: true,
                showError: false,
                showNotification: false,
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
        },
        watch: {
            showError: function (val) {
                if(!val) {
                    this.$store.commit('dismissError')
                }
            },
            showNotification: function (val) {
                if(!val) {
                    this.$store.commit('dismissNotification')
                }
            },
        },
      computed: {
        ...mapFields([
          'loading',
        ]),
      },
        created: function () {
            this.$store.watch(state => state.error, () => {
                if (this.$store.state.error) {
                    this.showError = true
                }
            })
            this.$store.watch(state => state.notification, () => {
                if (this.$store.state.notification) {
                    this.showNotification = true
                }
            })

        }
    }
</script>
