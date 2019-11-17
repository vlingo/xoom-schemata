import Vue from 'vue'
import Router from 'vue-router'
import Schemata from '@/views/Schemata'
import e404 from '@/components/e404'
import Editor from '@/components/Editor'
import Organization from '@/components/Organization'
import Unit from '@/components/Unit'
import Context from '@/components/Context'
import Schema from '@/components/Schema'
import SchemaVersion from '@/components/SchemaVersion'


Vue.use(Router)

export default new Router({
    routes: [
        {
            path: '/',
            name: 'home',
            component: Schemata
        },
        {
            path: '/schemata',
            name: 'schemata',
            component: Schemata
        },
        {
            path: '/editor',
            name: 'editor',
            component: Editor
        },
        {
            path: '/404',
            name: '404',
            component: e404
        },
        {
            path: '/organization',
            name: 'organization',
            component: Organization
        },
        {
            path: '/unit:unitId?',
            name: 'unit',
            component: Unit
        },
        {
            path: '/context',
            name: 'context',
            component: Context
        },
        {
            path: '/schema',
            name: 'schema',
            component: Schema
        },
        {
            path: '/schemaVersion',
            name: 'schemaVersion',
            component: SchemaVersion
        },
        {
            path: '/about',
            name: 'about',
            // route level code-splitting
            // this generates a separate chunk (about.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
        }
    ]
})
