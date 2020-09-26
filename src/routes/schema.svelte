<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { contextIdReturner, contextStringReturner, getCompatible, getFullyQualifiedName, initSelected, isCompatibleToContext, isCompatibleToOrg, isCompatibleToUnit, isStoreEmpty, orgIdReturner, orgStringReturner, schemaIdReturner, schemaStringReturner, selectStringsFrom, unitIdReturner, unitStringReturner } from '../utils';
	import errors from '../errors';

	let name;
	let description;
	let categorySelect = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category = "Event";
	let scopeSelect = ["Private", "Public"];
	let scope = "Public";


	let compatibleUnits;
	let compatibleContexts;
	let compatibleSchemas;

	let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
	$: organizationId = selectedOrg.id
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg.text);
		compatibleContexts = [];
		compatibleSchemas = [];

		fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
	$: unitId = selectedUnit.id;
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => u.unitId == unitId);
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit.text);
			compatibleSchemas = [];

			fullyQualified = getFullyQualifiedName("unit", $unitStore);
		}
	}

	let selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);
	$: contextId = selectedContext.id;
	$: if(contextId || !contextId) {
		if(organizationId && unitId) {
			$contextStore = ($contextsStore).find(c => c.contextId == contextId);
			compatibleSchemas = getCompatible($schemasStore, isCompatibleToContext, selectedContext.text);

			fullyQualified = getFullyQualifiedName("context", $contextStore);
		}
	}


	let selectedSchema = initSelected($schemaStore, schemaStringReturner, schemaIdReturner, $detailed);
	$: schemaId = selectedSchema.id;
	$: if(schemaId) {
		$schemaStore = ($schemasStore).find(s => s.schemaId == schemaId);
		
		fullyQualified = getFullyQualifiedName("schema", $schemaStore);
	}


	$: orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner, $detailed);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner, unitIdReturner, $detailed);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner, contextIdReturner, $detailed);
	$: schemaSelect = selectStringsFrom(compatibleSchemas, schemaStringReturner, schemaIdReturner, $detailed);
	
	
	let defineMode = isStoreEmpty(($schemasStore));
	let clearFlag = false;
	const newSchema = () => {
		name = "";
		description = "";
		category = "Event";
		scope = "Public";
		selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
		selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
		selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category);
	const updatable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category && schemaId);

	const define = () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, scope, category, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}

	const save = async () => {
		if(!updatable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.updateSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, schemaId, name, category, scope, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}

	function updateStores(obj, reset = false) {
		console.log({obj});
		$schemaStore = obj;
		if(reset) $schemasStore = ($schemasStore).filter(schema => schema.schemaId != schemaId);
		$schemasStore.push(obj);
	}
	function updateSelects() {
		// maybe also other compatibles..
		compatibleSchemas = getCompatible($schemasStore, isCompatibleToContext, selectedContext.text);
		schemaSelect = selectStringsFrom(compatibleSchemas, schemaStringReturner, schemaIdReturner, $detailed);
		selectedSchema = initSelected($schemaStore, schemaStringReturner, schemaIdReturner, $detailed);
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && category && scope && organizationId && unitId && contextId && defineMode) { //&& !schemaId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(schemaId) { isNextDisabled = false; }

	$: if(name && description && category && scope && organizationId && unitId && contextId && schemaId) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}
	
	let fullyQualified;
</script>

<CardForm title="Schema" linkToNext="New Schema Version" href="schemaVersion" on:new={newSchema} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
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