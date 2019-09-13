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
                    primary: "#00796B",
                    secondary: "#B2DFDB",
                    accent: "#009688",
                    error: "#ff5252",
                    warning: "#ee5757",
                    info: "#2196f3",
                    success: "#4caf50"
                }
            },
    },
    icons: {
        iconfont: 'md',
    }
})
