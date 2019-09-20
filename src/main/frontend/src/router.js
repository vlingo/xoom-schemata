import Vue from 'vue'
import Router from 'vue-router'
import Schemata from '@/views/Schemata'
import e404 from '@/components/e404'
import Editor from '@/components/Editor'

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
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import(/* webpackChunkName: "about" */ './views/About.vue')
    }
  ]
})
