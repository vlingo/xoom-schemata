<script>
	import { TextField, Textarea } from 'svelte-materialify/src';
	import CardForm from '../components/form/CardForm.svelte';
	import OrganizationSelect from '../components/form/OrganizationSelect.svelte';
	import UnitSelect from '../components/form/UnitSelect.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore } from '../stores';
	import { isEmpty } from '../utils';
	import { writable } from 'svelte/store';
	const notEmpty = (value) => !!value ? undefined : errors.EMPTY;

	let name;
	let description;

	let compatibleUnits = writable([]);

	let fullyQualified;

	function changedOrganization(store) {
		$compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = $compatibleUnits.length > 0 ? $compatibleUnits[$compatibleUnits.length-1] : undefined;
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
	const toggleDefineMode = () => {
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
		$compatibleUnits = $organizationStore ? $unitsStore.filter(u => u.organizationId == $organizationStore.organizationId) : [];
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

<CardForm title="Unit" linkToNext="New Context" prevLink="organization" on:new={toggleDefineMode} on:redefine={redefine} on:define={define}
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	<OrganizationSelect/>
	<UnitSelect {compatibleUnits}/>
	<TextField class="mb-4 pb-4" bind:value={name} rules={[notEmpty]}>Name</TextField>
	<Textarea bind:value={description} rules={[notEmpty]}>Description</Textarea>
</CardForm>
