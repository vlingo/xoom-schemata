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
    <v-btn color="info" @click="newOrganization">New</v-btn>
    <v-spacer></v-spacer>
    <v-btn color="primary" @click="saveOrganization"
           :disabled="!(valid && organizationId)">Save
    </v-btn>
    <v-btn color="primary" @click="createOrganization"
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

  watch: {
    $route(to, from) {
      let id = to.params.organizationId
      if (id) {
        this.loadOrganization(id)
      } else {
        this.clearForm()
      }
    },
  },

  methods: {
    createOrganization() {
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

            return created
          }
        )
        .then((created) => vm.$router.push({
            name: 'organization',
            params: {organizationId: created.organizationId}
          }
        ))
        .catch(function (err) {
          let response = err.response ? err.response.data + ' - ' : ''
          vm.$store.commit('raiseError', {message: response + err})
        })
    },
    newOrganization() {
      this.clearForm()
      this.$router.push('/organization/')
    },
    loadOrganization(organizationId) {
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
    saveOrganization() {
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
    let organizationIdToLoad = this.$route.params.organizationId
    if (organizationIdToLoad) {
      this.loadOrganization(organizationIdToLoad)
    }
  }
}
</script>
