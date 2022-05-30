<script>
	import { TextField, Textarea, Select } from 'svelte-materialify/src';
	import CardForm from '../components/form/CardForm.svelte';
	import OrganizationSelect from '../components/form/OrganizationSelect.svelte';
	import UnitSelect from '../components/form/UnitSelect.svelte';
	import ContextSelect from '../components/form/ContextSelect.svelte';
	import SchemaSelect from '../components/form/SchemaSelect.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { writable } from 'svelte/store';
	import { isEmpty } from '../utils';
	import errors from '../errors';

	const validName = (name) => {
		return /^[A-Z][a-zA-Z\d]*$/.test(name) ? undefined : errors.CLASSNAME;
	}
	const notEmpty = (value) => !!value ? undefined : errors.EMPTY;
	let categorySelect = [
		{ name: "Command",  value: ["Command"] },
		{ name: "Data",     value: ["Data"] },
		{ name: "Document", value: ["Document"] },
		{ name: "Envelope", value: ["Envelope"] },
		{ name: "Event",    value: ["Event"] },
		{ name: "Unknown",  value: ["Unknown"] },
	];
	let scopeSelect = [
		{ name: "Private", value: ["Private"] },
		{ name: "Public", value: ["Public"] },
	];

	let name;
	let description;
	let category = ["Event"];
	let scope = ["Public"];

	let compatibleUnits = writable([]);
	let compatibleContexts = writable([]);
	let compatibleSchemas = writable([]);

	let fullyQualified;
// 	$: fullyQualified = getFullyQualifiedName("organization", $organizationStore);

	function changedOrganization(store) {
		$compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = $compatibleUnits.length > 0 ? $compatibleUnits[$compatibleUnits.length-1] : undefined;
	}

	function changedUnit(store) {
		$compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = $compatibleContexts.length > 0 ? $compatibleContexts[$compatibleContexts.length-1] : undefined;
	}

	function changedContext(store) {
		$compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = $compatibleSchemas.length > 0 ? $compatibleSchemas[$compatibleSchemas.length-1] : undefined;
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
	const toggleDefineMode = () => {
		name = "";
		description = "";
		category = ["Event"];
		scope = ["Public"];

		defineMode = !defineMode;
	}

	const define = () => {
		if(!definable) return;
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, category[0], scope[0], description)
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
		$schemaStore = obj;
		if(reset) {
			$schemasStore.splice($schemasStore.findIndex(schema => schema.schemaId === $schemaStore.schemaId), 1, obj);
			$schemasStore = $schemasStore;
		} else {
			$schemasStore = [...$schemasStore, obj];
		}
	}
	function updateSelects() {
		$compatibleSchemas = $contextStore ? $schemasStore.filter(s => s.contextId == $contextStore.contextId) : [];
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

<CardForm title="Schema" linkToNext="New Schema Version"  prevLink="context" href="schemaVersion" on:new={toggleDefineMode} on:redefine={redefine} on:define={define}
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	<OrganizationSelect/>
	<UnitSelect {compatibleUnits}/>
	<ContextSelect {compatibleContexts}/>
	{#if !defineMode}
		<SchemaSelect {compatibleSchemas}/>
	{/if}
	<span class="flex-two-col">
		<Select class="flex-child" mandatory bind:value={category} items={categorySelect}>Category</Select>
		<Select class="flex-child" mandatory bind:value={scope} items={scopeSelect}>Scope</Select>
	</span>
	<TextField class="mb-4 pb-4" bind:value={name} rules={[notEmpty, validName]}>Name</TextField>
	<Textarea bind:value={description} rules={[notEmpty]}>Description</Textarea>
</CardForm>


<style>
	.flex-two-col {
		display: flex;
	}
</style>
