<script>
	import { TextField, Textarea } from 'svelte-materialify/src';
	import CardForm from '../components/form/CardForm.svelte';
	import OrganizationSelect from '../components/form/OrganizationSelect.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import { isEmpty } from '../utils';
	const notEmpty = (value) => !!value ? undefined : errors.EMPTY;

	let name;
	let description;

	let fullyQualified;

	function changedOrganization(store) {
		if(store) {
			name = store.name;
			description = store.description;
		} else {
			name = "";
			description = "";
		}
	}

	const define = async () => {
		if(!definable) return;
		SchemataRepository.createOrganization(name, description)
			.then(created => {
				updateStores(created);
				defineMode = false;
			})
	}

	const redefine = async () => {
		if(!redefinable) return;
		SchemataRepository.updateOrganization(($organizationStore).organizationId, name, description)
			.then(updated => {
				updateStores(updated, true);
			})
	}
	function updateStores(obj, reset = false) {
		$organizationStore = obj;
		if(reset) {
			$organizationsStore.splice($organizationsStore.findIndex(org => org.organizationId === $organizationStore.organizationId), 1, obj);
			$organizationsStore = $organizationsStore;
		} else {
			$organizationsStore = [...$organizationsStore, obj];
		}
	}

	let defineMode = isEmpty(($organizationsStore));
	const toggleDefineMode = () => {
		name = "";
		description = "";
    defineMode = !defineMode;
	}

	$: changedOrganization($organizationStore);
	$: definable = name && description;
	$: redefinable = definable && $organizationStore;
	$: showNewButton = $organizationsStore.length > 0;
</script>

<svelte:head>
	<title>Organization</title>
</svelte:head>

<CardForm title="Organization" linkToNext="New Unit" on:new={toggleDefineMode} on:redefine={redefine} on:define={define}
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	{#if !defineMode}
		<OrganizationSelect/>
	{/if}
	<TextField class="mb-4 pb-4" bind:value={name} rules={[notEmpty]}>Name</TextField>
	<Textarea bind:value={description} rules={[notEmpty]}>Description</Textarea>
</CardForm>
