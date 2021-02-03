<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import { isEmpty } from '../utils';

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
		if(reset) $organizationsStore = ($organizationsStore).filter(org => org.organizationId != ($organizationStore).organizationId);
    $organizationsStore = [...$organizationsStore, obj];
	}
	
	let defineMode = isEmpty(($organizationsStore));
	const newOrg = () => {
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

<CardForm title="Organization" linkToNext="New Unit" on:new={newOrg} on:redefine={redefine} on:define={define} 
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	{#if !defineMode}
		<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	{/if}
	<ValidatedInput label="Name" bind:value={name}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description}/>
</CardForm>