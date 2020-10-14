<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';
	const validName = (name) => {
		return /^[A-Z][a-zA-Z\d]*$/.test(name) ? undefined : errors.CLASSNAME;
	}

	let name = $schemaStore? $schemaStore.name : "";
	let description = $schemaStore? $schemaStore.description : "";
	const items = [
    { name: 'Foo', value: 'foo' },
    { name: 'Bar', value: 'bar' },
    { name: 'Fizz', value: 'fizz' },
    { name: 'Buzz', value: 'buzz' },
  ];
	let categorySelect = [
		{ name: "Command", value: "Command" },
		{ name: "Data", value: "Data" },
		{ name: "Document", value: "Document" },
		{ name: "Envelope", value: "Envelope" },
		{ name: "Event", value: "Event" },
		{ name: "Unknown", value: "Unknown" },
	];
	let category = $schemaStore? [$schemaStore.category] : ["Event"];
	let scopeSelect = [
		{ name: "Private", value: "Private" },
		{ name: "Public", value: "Public" },
	];
	let scope = $schemaStore? [$schemaStore.scope] : ["Public"];

	let compatibleUnits = [];
	let compatibleContexts = [];
	let compatibleSchemas = [];
	$: changedUnits($organizationStore);
	function changedUnits(store) {
		console.log({store});
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	$: changedContexts($unitStore);
	function changedContexts(store) {
		console.log({store});
		compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = compatibleContexts.length > 0 ? compatibleContexts[compatibleContexts.length-1] : undefined;
	}
	$: changedSchemas($contextStore);
	function changedSchemas(store) {
		console.log({store});
		compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = compatibleSchemas.length > 0 ? compatibleSchemas[compatibleSchemas.length-1] : undefined;
	}

	// 	fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	
	let defineMode = isStoreEmpty(($schemasStore));
	let clearFlag = false;
	const newSchema = () => {
		name = "";
		description = "";
		category = ["Event"];
		scope = ["Public"];

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category);
	const updatable = () => (name && description && $organizationStore && $unitStore && $contextStore && scope && category && $schemaStore);

	const define = () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, scope[0], category[0], description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}

	const save = async () => {
		if(!updatable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.updateSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, name, category[0], scope[0], description)
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

	$: if(!validName(name) && name && description && category && scope && $organizationStore && $unitStore && $contextStore && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(!defineMode) { isNextDisabled = false; }

	$: if(!validName(name) && name && description && category && scope && $organizationStore && $unitStore && $contextStore && $schemaStore) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}
	
	let fullyQualified;
</script>

<svelte:head>
	<title>Schema</title>
</svelte:head>

<CardForm title="Schema" linkToNext="New Schema Version" href="schemaVersion" on:new={newSchema} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
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