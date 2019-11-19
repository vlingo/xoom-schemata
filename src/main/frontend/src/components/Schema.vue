<template>
    <v-card class="xs12" height="95vh" width="100%" id="schemata-view-schema">
        <v-card-title>Schema</v-card-title>
        <v-card-text>
            <v-form
                    ref="form"
                    v-model="valid"
            >
                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="schemaId"
                                label="SchemaID"
                                disabled
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="organizations"
                                label="Organization"
                                :rules="[rules.notEmpty]"
                                :loading="loading.organizations"
                                return-object
                                item-value="organizationId"
                                item-text="name"
                                v-model="organizationId"
                                @input="o => $store.dispatch('select',o)"
                                :disabled="schemaId !== undefined"
                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="units"
                                label="Unit"
                                :rules="[rules.notEmpty]"
                                :loading="loading.units"
                                return-object
                                item-value="unitId"
                                item-text="name"
                                v-model="unitId"
                                @input="u => $store.dispatch('select',u)"
                                :disabled="schemaId !== undefined"
                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="contexts"
                                label="Context"
                                :loading="loading.contexts"
                                :rules="[rules.notEmpty]"
                                return-object
                                item-value="contextId"
                                item-text="namespace"
                                v-model="contextId"
                                @input="c => $store.dispatch('select',c)"
                                :disabled="schemaId !== undefined"
                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12">
                        <v-text-field
                                v-model="name"
                                :rules="[rules.notEmpty]"
                                label="Name"
                                required
                        ></v-text-field>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="categories"
                                label="Category"
                                :rules="[rules.notEmpty]"
                                :loading="loading.categories"
                                v-model="category"
                        ></v-autocomplete>
                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <v-autocomplete
                                :items="scopes"
                                label="Scope"
                                :rules="[rules.notEmpty]"
                                :loading="loading.scopes"
                                v-model="scope"
                        ></v-autocomplete>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col class="d-flex" cols="12">
                        <v-textarea
                                v-model="description"
                                :rules="[rules.notEmpty]"
                                label="Description"
                                required
                        ></v-textarea>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-btn color="info" @click="clearForm">New</v-btn>
            <v-spacer></v-spacer>
          <v-btn color="primary" @click="save"
                 :disabled="!(valid && schemaId)">Save
          </v-btn>
            <v-btn color="primary"
                   :disabled="!(valid && !schemaId)"
                   @click="create">Create
            </v-btn>
            <v-btn color="secondary" to="/schemaVersion"
                   :disabled="!schemaId">Create Schema Version
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import selectboxLoaders from '@/mixins/selectbox-loaders'
    import validationRules from '@/mixins/form-validation-rules'
    import Repository from '@/api/SchemataRepository'

    export default {
        mixins: [selectboxLoaders, validationRules],

        data: () => {
            return {
                organizationId: undefined,
                unitId: undefined,
                contextId: undefined,
                schemaId: undefined,
                name: '',
                description: '',
                category: '',
                scope: '',
            }
        },

        methods: {
            create() {
              let vm = this
              Repository.createSchema(
                this.$store.getters.organizationId,
                this.$store.getters.unitId,
                this.$store.getters.contextId,
                this.name,
                this.scope,
                this.category,
                this.description)
                .then((created) => {
                    vm.schema = created
                    vm.schemaId = created.schemaId
                    vm.name = created.name
                    vm.scope = created.scope
                    vm.category = created.category
                    vm.description = created.description
                    vm.$store.dispatch('select', created)

                    vm.$store.commit('raiseNotification', {
                      message: `Schema '${vm.name}' created.`,
                      type: 'success'
                    })
                    vm.$store.dispatch('select', created)
                  }
                )
                .catch(function (err) {
                  let response = err.response ? err.response.data + ' - ' : ''
                  vm.$store.commit('raiseError', {message: response + err})
                })
            },
            save() {
              let vm = this
              Repository.updateSchema(
                this.$store.getters.organizationId,
                this.$store.getters.unitId,
                this.$store.getters.contextId,
                this.schemaId,
                this.name, this.category, this.scope, this.description)
                .then((updated) => {
                  vm.$store.dispatch('select', updated)
                  vm.$store.commit('raiseNotification', {
                    message: `Unit ${vm.name} updated.`,
                    type: 'success'
                  })
                })
            },
            load(organizationId, unitId, contextId, schemaId) {
              let vm = this
              Repository.getSchema(organizationId, unitId, contextId, schemaId)
                .then((loaded) => {
                  vm.organizationId = loaded.organizationId
                  vm.unitId = loaded.unitId
                  vm.contextId = loaded.contextId
                  vm.name = loaded.name
                  vm.category = loaded.category
                  vm.scope = loaded.scope
                  vm.description = loaded.description
                })
            }
        },
      mounted() {
        this.organizationId = this.$store.getters.organizationId
        this.unitId = this.$store.getters.unitId
        this.contextId = this.$store.getters.contextId
        let schemaId = this.$store.getters.schemaId
        if (this.organizationId && this.unitId && this.contextId && schemaId) {
          this.load(this.organizationId, this.unitId, this.contextId, schemaId)
        }
      }
    }
</script>
