<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { isEmpty } from '../utils';
	import errors from '../errors';
	const validName = (name) => {
		return /^[A-Z][a-zA-Z\d]*$/.test(name) ? undefined : errors.CLASSNAME;
	}
	let categorySelect = [
		{ name: "Command", value: "Command" },
		{ name: "Data", value: "Data" },
		{ name: "Document", value: "Document" },
		{ name: "Envelope", value: "Envelope" },
		{ name: "Event", value: "Event" },
		{ name: "Unknown", value: "Unknown" },
	];
	let scopeSelect = [
		{ name: "Private", value: "Private" },
		{ name: "Public", value: "Public" },
	];

	let name;
	let description;
	let category = ["Event"];
	let scope = ["Public"];

	let compatibleUnits = [];
	let compatibleContexts = [];
	let compatibleSchemas = [];

	let fullyQualified;
// 	$: fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	
	function changedOrganization(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	
	function changedUnit(store) {
		compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = compatibleContexts.length > 0 ? compatibleContexts[compatibleContexts.length-1] : undefined;
	}
	
	function changedContext(store) {
		compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = compatibleSchemas.length > 0 ? compatibleSchemas[compatibleSchemas.length-1] : undefined;
	}
	
	function changedSchema(store) {
		if(store) {
			name = store.name;
			description = store.description;
			category = [store.category];
			scope = [store.scope];
		} else {
			name = "";
			description = "";
			category = ["Event"];
			scope = ["Public"];
		}
	}
	
	let defineMode = isEmpty(($schemasStore));
	const newSchema = () => {
		name = "";
		description = "";
		category = ["Event"];
		scope = ["Public"];

		defineMode = !defineMode;
	}

	const define = () => {
		if(!definable) return;
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, scope[0], category[0], description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}

	const redefine = async () => {
		if(!redefinable) return;
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
		$schemasStore = [...$schemasStore, obj];
	}
	function updateSelects() {
		// maybe also other compatibles..
		compatibleSchemas = $contextStore ? $schemasStore.filter(s => s.contextId == $contextStore.contextId) : [];
	}

	$: changedOrganization($organizationStore);
	$: changedUnit($unitStore);
	$: changedContext($contextStore);
	$: changedSchema($schemaStore);
	$: definable = !validName(name) && name && description && $organizationStore && $unitStore && $contextStore && scope[0] && category[0];
	$: redefinable = definable && $schemaStore;
  $: showNewButton = $schemasStore.length > 0;
</script>

<svelte:head>
	<title>Schema</title>
</svelte:head>

<CardForm title="Schema" linkToNext="New Schema Version"  prevLink="context" href="schemaVersion" on:new={newSchema} on:redefine={redefine} on:define={define} 
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	{#if !defineMode}
		<Select label="Schema" storeOne={schemaStore} storeAll={schemasStore} arrayOfSelectables={compatibleSchemas} containerClasses="folder-inset3"/>
	{/if}
	
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Category" bind:value={category} options={categorySelect}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} options={scopeSelect}/>
	</span>
	<ValidatedInput label="Name" bind:value={name} validator={validName} invalidString={errors.CLASSNAME}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description}/>
</CardForm>


<style>
	.flex-two-col {
		display: flex;
	}
</style>