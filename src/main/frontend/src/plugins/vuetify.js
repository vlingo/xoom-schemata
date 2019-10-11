import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

export default new Vuetify({
    theme: {
        dark: false,
        themes:
            {
                light: {
                    primary: "#00753B",
                    secondary: "#B2DFDB",
                    accent: "#009688",
                    error: "#e64325",
                    warning: "#ffa336",
                    info: "#22bee6",
                    success: "#acd164"
                }
            },
    },
    icons: {
        iconfont: 'mdiSvg',
    }
})
