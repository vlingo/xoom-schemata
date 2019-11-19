import Repository from '@/api/SchemataRepository'
import {mapFields} from 'vuex-map-fields';

export default {
    data: () => {
        return {
            loading: {
                organizations: false,
                units: false,
                contexts: false,
                versions: false,
                schemata: false,
            }
        }
    },
    asyncComputed: {
        async organizations() {
            this.loading.organizations = true
            const result = await Repository.getOrganizations()
            this.loading.organizations = false
            return result
        },
        async units() {
            if (!this.$store.getters.organizationId) return []

            this.loading.organizations = true
            const result = await Repository.getUnits(this.$store.getters.organizationId)
            this.loading.organizations = false
            return result
        },
        async contexts() {
            if (!this.$store.getters.organizationId || !this.$store.getters.unitId) return []

            this.loading.contexts = true
            const result = await Repository.getContexts(
                this.$store.getters.organizationId,
                this.$store.getters.unitId
            )
            this.loading.contexts = false
            return result
        },
        async schemata() {
            if (!this.$store.getters.organizationId || !this.$store.getters.unitId || !this.$store.getters.contextId) return []

            this.loading.schemata = true
            const result = await Repository.getSchemata(
                this.$store.getters.organizationId,
                this.$store.getters.unitId,
                this.$store.getters.contextId,
            )

            this.loading.schemata = false
            return result
        },
        async categories() {
            this.loading.categories = true
            const result = await Repository.getCategories()
            this.loading.categories = false
            return result
        },
        async scopes() {
            this.loading.scopes = true
            const result = await Repository.getScopes()
            this.loading.scopes = false
            return result
        },
    },
}
