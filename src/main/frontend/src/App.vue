<template>
    <v-app>
        <v-navigation-drawer
                v-model="drawer"
                app
        >
            <v-list dense>
                <v-list-item @click="">
                    <v-list-item-action>
                        <v-icon>home</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>Home</v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>

        <v-app-bar dense>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
            <v-toolbar-title class="headline text-uppercase">
                <span>Vlingo</span><span class="font-weight-light">Schemata</span>
            </v-toolbar-title>
        </v-app-bar>

        <v-content>
            <v-alert class="mt-2 ml-2 mr-2 mb-0" v-if="$store.state.error" dismissible type="error">{{$store.state.error.message}}</v-alert>
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
                drawer: false
            }
        }
    }
</script>
