<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Select from '../components/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';
	const validName = (name) => {
		return /^[A-Z][a-zA-Z\d]*$/.test(name);
	}

	let name = $schemaStore? $schemaStore.name : "";
	let description = $schemaStore? $schemaStore.description : "";
	let categorySelect = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category = $schemaStore? $schemaStore.category : "Event";
	let scopeSelect = ["Private", "Public"];
	let scope = $schemaStore? $schemaStore.scope : "Public";


	$: compatibleUnits = changedUnits($organizationStore)
	function changedUnits(store) {
		store ? $unitStore = $unitsStore.find(u => u.organizationId == store.organizationId) : $unitStore = undefined;
		return store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
	}
	$: compatibleContexts = changedContexts($unitStore)
	function changedContexts(store) {
		store ? $contextStore = $contextsStore.find(c => c.unitId == store.unitId) : $contextStore = undefined;
		return store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
	}
	$: compatibleSchemas = changedSchemas($contextStore);
	function changedSchemas(store) {
		store ? $schemaStore = $schemasStore.find(s => s.contextId == store.contextId) : $schemaStore = undefined;
		return store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
	}

	// 	fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	
	let defineMode = isStoreEmpty(($schemasStore));
	let clearFlag = false;
	const newSchema = () => {
		name = "";
		description = "";
		category = "Event";
		scope = "Public";

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category);
	const updatable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category && $schemaStore);

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
		SchemataRepository.updateSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, name, category, scope, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}

	function updateStores(obj, reset = false) {
		console.log({obj});
		$schemaStore = obj;
		if(reset) $schemasStore = ($schemasStore).filter(schema => schema.schemaId !=  ($schemaStore).schemaId);
		$schemasStore.push(obj);
	}
	function updateSelects() {
		// maybe also other compatibles..
		compatibleSchemas = $contextStore ? $schemasStore.filter(s => s.contextId == $contextStore.contextId) : [];
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(validName(name) && name && description && category && scope && $organizationStore && $unitStore && $contextStore && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if($schemaStore) { isNextDisabled = false; }

	$: if(validName(name) && name && description && category && scope && $organizationStore && $unitStore && $contextStore && $schemaStore) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}
	
	let fullyQualified;
</script>

<CardForm title="Schema" linkToNext="New Schema Version" href="schemaVersion" on:new={newSchema} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	{#if !defineMode}
		<Select label="Schema" storeOne={schemaStore} storeAll={schemasStore} arrayOfSelectables={compatibleSchemas} containerClasses="folder-inset3"/>
	{/if}
	
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Category" bind:value={category} {clearFlag} options={categorySelect}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} {clearFlag} options={scopeSelect}/>
	</span>
	<ValidatedInput label="Name" bind:value={name} {clearFlag} validator={validName} invalidString={errors.CLASSNAME}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>


<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>