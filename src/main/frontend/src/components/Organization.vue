<template>
<v-card class="xs12" height="95vh" width="100%" id="schemata-view-organization">
  <v-card-title>Organization</v-card-title>
  <v-card-text>
    <v-form
      ref="form"
      v-model="valid"
    >
      <v-row>
        <v-col class="d-flex" cols="12">
          <v-text-field
            v-model="organizationId"
            label="OrganizationID"
            disabled
          ></v-text-field>
        </v-col>
        <v-col class="d-flex" cols="12">
          <v-text-field
            v-model="name"
            :rules="[rules.notEmpty]"
            label="Name"
            required
          ></v-text-field>
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
           :disabled="!(valid && organizationId)">Save
    </v-btn>
    <v-btn color="primary" @click="create"
           :disabled="!(valid && !organizationId)">Create
    </v-btn>
    <v-btn color="secondary" to="/unit"
           :disabled="!organizationId">Create Unit
    </v-btn>
  </v-card-actions>
</v-card>
</template>

<script>
import Repository from '@/api/SchemataRepository'
import selectboxLoaders from '@/mixins/selectbox-loaders'
import validationRules from '@/mixins/form-validation-rules'

export default {
  mixins: [selectboxLoaders, validationRules],

  data: () => {
    return {
      organizationId: undefined,
      name: '',
      description: '',
    }
  },

  methods: {
    create() {
      let vm = this
      Repository.createOrganization(this.name, this.description)
        .then((created) => {
            vm.organization = created
            vm.organizationId = created.organizationId
            vm.name = created.name
            vm.description = created.description

            vm.$store.commit('raiseNotification', {
              message: `Organization '${vm.name}' created.`,
              type: 'success'
            })
            vm.$store.dispatch('select', created)

            return created
          }
        )
        .catch(function (err) {
          let response = err.response ? err.response.data + ' - ' : ''
          vm.$store.commit('raiseError', {message: response + err})
        })
    },
    load(organizationId) {
      let vm = this
      Repository.getOrganization(organizationId)
        .then((loaded) => {
            vm.organization = loaded
            vm.organizationId = loaded.organizationId
            vm.name = loaded.name
            vm.description = loaded.description
          }
        )
        .catch(function (err) {
          let response = err.response ? err.response.data + ' - ' : ''
          vm.$store.commit('raiseError', {message: response + err})
        })
    },
    save() {
      let vm = this
      Repository.updateOrganization(this.organizationId, this.name, this.description)
        .then(() => {
          vm.$store.commit('raiseNotification', {
            message: `Organization ${vm.name} updated.`,
            type: 'success'
          })
        })
    }
  },

  mounted() {
    let orgId = this.$store.getters.organizationId
    if (orgId) {
      this.load(orgId)
    }
  }
}
</script>
