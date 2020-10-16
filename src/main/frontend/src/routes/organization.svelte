<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';

	let name;
	let description;

	$: changedOrganization($organizationStore);
	function changedOrganization(store) {
		if(store) {
			name = store.name;
			description = store.description;
		}
	}

	const definable = () => (name && description );
	const updatable = () => (name && description && $organizationStore);

	const define = async () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createOrganization(name, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}

	const save = async () => {
		if(!updatable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.updateOrganization(($organizationStore).organizationId, name, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}
	function updateStores(obj, reset = false) {
		console.log({obj});
		$organizationStore = obj;
		if(reset) $organizationsStore = ($organizationsStore).filter(org => org.organizationId != ($organizationStore).organizationId);
		$organizationsStore.push(obj);
	}
	function updateSelects() {
		//not needed
	}
	
	let defineMode = isStoreEmpty(($organizationsStore)); //maybe this doesn't work anymore, maybe .length,...
	const newOrg = () => {
		name = "";
		description = "";
		
		defineMode = true;
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(!defineMode) { isNextDisabled = false; }
	
	$: if($organizationStore && name && description) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>

<svelte:head>
	<title>Organization</title>
</svelte:head>

<CardForm title="Organization" linkToNext="New Unit" on:new={newOrg} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	{#if !defineMode}
		<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	{/if}
	<ValidatedInput label="Name" bind:value={name}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description}/>
</CardForm>