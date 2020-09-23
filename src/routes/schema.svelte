<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { contextStringReturner, getCompatible, getId, initSelected, isCompatibleToContext, isCompatibleToOrg, isCompatibleToUnit, isStoreEmpty, orgStringReturner, schemaStringReturner, selectStringsFrom, unitStringReturner } from '../utils';
import errors from '../errors';

	let id;
	let name;
	let description;
	let categorySelect = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category = "Event";
	let scopeSelect = ["Private", "Public"];
	let scope = "Public";


	let compatibleUnits;
	let compatibleContexts;
	let compatibleSchemas;

	let selectedOrg = initSelected($organizationStore, orgStringReturner);
	$: organizationId = getId(selectedOrg);
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
		compatibleContexts = [];
		compatibleSchemas = [];

		console.log("in orgId");
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner);
	$: unitId = getId(selectedUnit);
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit);
			compatibleSchemas = [];
		}
		console.log("in unitId");
	}

	let selectedContext = initSelected($contextStore, contextStringReturner);
	$: contextId = getId(selectedContext);
	$: if(contextId || !contextId) {
		if(organizationId && unitId) {
			$contextStore = ($contextsStore).find(c => isCompatibleToUnit(c));
			compatibleSchemas = getCompatible($schemasStore, isCompatibleToContext, selectedContext);
		}
		console.log("in contextId");
	}


	let selectedSchema = initSelected($schemaStore, schemaStringReturner);
	$: schemaId = getId(selectedSchema);
	$: if(schemaId) $schemaStore = ($schemasStore).find(s => isCompatibleToContext(s));


	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner);
	$: schemaSelect = selectStringsFrom(compatibleSchemas, schemaStringReturner);


	// let compatibleUnits;
	// let compatibleContexts;

	// let selectedOrg = initSelected($organizationStore, orgStringReturner);
	// $: organizationId = getId(selectedOrg);
	// $: if(organizationId || !organizationId) {
	// 	$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
	// 	compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
	// 	// clearing is necessary for grandchild
	// 	compatibleContexts = [];

	// 	console.log("in orgId");
	// }

	// let selectedUnit = initSelected($unitStore, unitStringReturner);
	// $: unitId = getId(selectedUnit);
	// $: if(unitId || !unitId) {
	// 	if(organizationId) {
	// 		$unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));
	// 		compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit);
	// 	}
	// 	console.log("in unitId");
	// }

	// let selectedContext = initSelected($contextStore, contextStringReturner);
	// $: contextId = getId(selectedContext);
	// $: if(contextId) $contextStore = ($contextsStore).find(c => isCompatibleToUnit(c));


	// const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	// $: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);
	// $: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner);
	
	let defineMode = isStoreEmpty(($schemasStore));
	let clearFlag = false;
	const newSchema = () => {
		id = "";
		name = "";
		description = "";
		category = "Event";
		scope = "Public";
		selectedOrg = initSelected($organizationStore, orgStringReturner);
		selectedUnit = initSelected($unitStore, unitStringReturner);
		selectedContext = initSelected($contextStore, contextStringReturner);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const define = () => {
		if(!name || !description || !$organizationStore || !$unitStore || !$contextStore || !scope || !category) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, scope, category, description)
			.then(created => {
				console.log({created});
				$schemaStore = created;
				$schemasStore.push(created);
				
				schemaSelect = selectStringsFrom($schemasStore, schemaStringReturner);
				selectedSchema = initSelected($schemaStore, schemaStringReturner);
			})
		defineMode = false;
	}

	const save = async () => {
		if(!schemaId || !name || !description || !scope || !category || !$organizationStore || !$unitStore || !$contextStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.updateSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, schemaId, name, category, scope, description)
			.then(updated => {
				console.log({updated});
				$schemaStore = updated;
				$schemasStore = ($schemasStore).filter(schema => schema.schemaId != schemaId);
				$schemasStore.push(updated);
				
				schemaSelect = selectStringsFrom($schemasStore, schemaStringReturner);
				selectedSchema = initSelected($schemaStore, schemaStringReturner);
			})
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && category && scope && organizationId && unitId && contextId && defineMode) { //&& !schemaId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(schemaId) {
		isNextDisabled = false;
	}

	$: if(name && description && category && scope && organizationId && unitId && contextId && schemaId) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}
</script>

<CardForm title="Schema" linkToNext="New Schema Version" href="schemaVersion" on:new={newSchema} on:save={save} on:define={define} {isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode}>
	<!-- <span class="flex-two-col"> -->
		<ValidatedInput inline containerClasses="" type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput inline containerClasses="folder-inset1" type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	<!-- </span> -->
	<ValidatedInput inline containerClasses="folder-inset2" type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
	{#if !defineMode}
		<ValidatedInput inline containerClasses="folder-inset3" disabled={defineMode} type="select" label="Schema" bind:value={selectedSchema} {clearFlag} options={schemaSelect}/>
	{/if}
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Category" bind:value={category} {clearFlag} options={categorySelect}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} {clearFlag} options={scopeSelect}/>
	</span>
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>


<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>