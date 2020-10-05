<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Select from '../components/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';

	let name = $organizationStore? $organizationStore.name : "";
	let description = $organizationStore? $organizationStore.description : "";

	// let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner);
	// $: organizationId = selectedOrg.id;
	// $: if(organizationId || !organizationId) {
	// 	$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);

	// 	fullyQualified = getFullyQualifiedName("unit", $organizationStore);
	// } 

	// $: orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner, $detailed);

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
	// maybe look into this, I just don't know enough stuff - document.getElementById("myForm").reset();
	let clearFlag = false;
	const newOrg = () => {
		name = "";
		description = "";
		
		defineMode = true;
		clearFlag = !clearFlag;
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if($organizationStore) { isNextDisabled = false; }
	
	$: if($organizationStore && name && description) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>


<CardForm title="Organization" linkToNext="New Unit" on:new={newOrg} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	{#if !defineMode}
		<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore}/>
	{/if}
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>