<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore, detailed } from '../stores';
	import { getCompatible, getFullyQualifiedName, initSelected, isCompatibleToOrg, isStoreEmpty, orgIdReturner, orgStringReturner, selectStringsFrom, unitIdReturner, unitStringReturner } from '../utils';
	import errors from '../errors';

	let name;
	let description;

	let compatibleUnits = [];

	let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
	$: organizationId = selectedOrg.id;
	$: if(organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg.text);

		fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
	$: unitId = selectedUnit.id;
	$: if(unitId) {
		$unitStore = ($unitsStore).find(u => u.unitId == unitId);

		fullyQualified = getFullyQualifiedName("unit", $unitStore);
	}


	$: orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner, $detailed);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner, unitIdReturner, $detailed);
	

	let defineMode = isStoreEmpty(($unitsStore));
	let clearFlag = false;
	const newUnit = () => {
		name = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	// maybe the unitId should also come from the store (if we validate the value before pushing it to the store, for example.)
	// can just be changed here and work the same
	// ++organizationStore is always an object, right? so it always returns true.. so check emptyness instead, probably
	const definable = () => (name && description && $organizationStore);
	const updatable = () => (name && description && $organizationStore && unitId);

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
		SchemataRepository.updateUnit(($organizationStore).organizationId, unitId, name, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}
	function updateStores(obj, reset = false) {
		console.log({obj});
		$unitStore = obj;
		if(reset) $unitsStore = ($unitsStore).filter(unit => unit.unitId != unitId);
		$unitsStore.push(obj);
	}
	function updateSelects() {
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg.text);
		unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner, unitIdReturner, $detailed);
		selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && organizationId && defineMode) { //&& !unitId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(unitId) { isNextDisabled = false; }

	$: if(unitId && organizationId && name && description) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>

<CardForm title="Unit" linkToNext="New Context" on:new={newUnit} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<ValidatedInput inline containerClasses="" type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
	{#if !defineMode}
		<ValidatedInput inline containerClasses="folder-inset1" disabled={defineMode} type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	{/if}
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>