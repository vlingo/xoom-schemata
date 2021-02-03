<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore } from '../stores';
	import { isEmpty } from '../utils';

	let name;
	let description;

	let compatibleUnits = [];

	let fullyQualified;
	
	function changedOrganization(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	
	function changedUnit(store) {
		if(store) {
			name = store.name;
			description = store.description;
		} else {
			name = "";
			description = "";
		}
	}

	let defineMode = isEmpty(($unitsStore));
	const newUnit = () => {
		name = "";
		description = "";

		defineMode = !defineMode;
	}

	const define = async () => {
		if(!definable) return;
		SchemataRepository.createUnit(($organizationStore).organizationId, name, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}
	const redefine = async () => {
		if(!redefinable) return;
		SchemataRepository.updateUnit(($organizationStore).organizationId, ($unitStore).unitId, name, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}
	function updateStores(obj, reset = false) {
		console.log({obj});
		$unitStore = obj;
		if(reset) $unitsStore = ($unitsStore).filter(unit => unit.unitId != ($unitStore).unitId);
		$unitsStore = [...$unitsStore, obj];
	}
	function updateSelects() {
		compatibleUnits = $organizationStore ? $unitsStore.filter(u => u.organizationId == $organizationStore.organizationId) : [];
	}

	$: changedOrganization($organizationStore)
	$: changedUnit($unitStore);
	$: definable = name && description && $organizationStore;
	$: redefinable = definable && $unitStore;
  $: showNewButton = $unitsStore.length > 0;
</script>

<svelte:head>
	<title>Unit</title>
</svelte:head>

<CardForm title="Unit" linkToNext="New Context" on:new={newUnit} on:redefine={redefine} on:define={define} 
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	{#if !defineMode}
		<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	{/if}
	<ValidatedInput label="Name" bind:value={name}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description}/>
</CardForm>