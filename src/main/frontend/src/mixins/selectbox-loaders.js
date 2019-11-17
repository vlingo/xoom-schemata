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
    computed: {
        ...mapFields([
            'organization',
            'unit',
            'context',
            'schema',
            'schemaVersion'
        ]),
    },
    asyncComputed: {
        async organizations() {
            this.loading.organizations = true
            const result = await Repository.getOrganizations()
            this.loading.organizations = false
            return result
        },
        async units() {
            if (!this.organization) return []

            this.loading.organizations = true
            const result = await Repository.getUnits(this.organization.organizationId)
            this.loading.organizations = false
            return result
        },
        async contexts() {
            if (!this.organization || !this.unit) return []

            this.loading.contexts = true
            const result = await Repository.getContexts(
                this.organization.organizationId,
                this.unit.unitId
            )
            this.loading.contexts = false
            return result
        },
        async schemata() {
            if (!this.organization || !this.unit || !this.context) return []

            this.loading.schemata = true
            const result = await Repository.getSchemata(
                this.organization.organizationId,
                this.unit.unitId,
                this.context.contextId,
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
