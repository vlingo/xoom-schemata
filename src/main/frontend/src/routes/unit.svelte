<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Select from '../components/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';

	let name = $unitStore? $unitStore.name : "";
	let description = $unitStore? $unitStore.description : "";

	let compatibleUnits = [];

	$: changedUnits($organizationStore)
	function changedUnits(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}

	let defineMode = isStoreEmpty(($unitsStore));
	let clearFlag = false;
	const newUnit = () => {
		name = "";
		description = "";

		defineMode = true;
		clearFlag = !clearFlag;
	}

	// ++organizationStore is always an object, right? so it always returns true.. so check emptyness instead, probably - or have it be undefined instead of empty object
	const definable = () => (name && description && $organizationStore);
	const updatable = () => (name && description && $organizationStore && $unitStore);

	const define = async () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createUnit(($organizationStore).organizationId, name, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}
	const save = async () => {
		if(!updatable) { console.log(errors.SUBMIT); return; }
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
		$unitsStore.push(obj);
	}
	function updateSelects() {
		compatibleUnits = $organizationStore ? $unitsStore.filter(u => u.organizationId == $organizationStore.organizationId) : [];
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && $organizationStore && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if($unitStore) { isNextDisabled = false; }

	$: if(name && description && $organizationStore && $unitStore) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>

<CardForm title="Unit" linkToNext="New Context" on:new={newUnit} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore}/>
	{#if !defineMode}
		<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	{/if}
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>