export default {
    data: () => {
        return {
            valid: false,
            rules: {
                notEmpty: v => !!v || 'may not be empty',
                versionNumber: v => /^\d+\.\d+\.\d+$/.test(v) || 'must be a semantic version number (major.minor.patch, e.g. 1.6.12)',

            }
        }
    },

    methods: {
        validate() {
            if (this.$refs.form.validate()) {
                this.valid = true
            }
        },
        reset() {
            this.$refs.form.reset()
        },
        resetValidation() {
            this.$refs.form.resetValidation()
        },
    },

}