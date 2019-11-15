import Vue from 'vue'
import Vuetify from 'vuetify/lib'
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

export default new Vuetify({
    theme: {
        dark: false,
        themes:
            {
                light: {
                    primary: "#00753B",
                    secondary: "#acd164",
                    accent: "#B78BC0",
                    error: "#e64325",
                    warning: "#ffa336",
                    info: "#22bee6",
                    success: "#009D4B"
                }
            },
    },
    icons: {
        iconfont: 'mdiSvg',
    }
})
