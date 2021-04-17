<script>
	import { TextField, Textarea, Select } from 'svelte-materialify/src';
	import CardForm from '../components/form/CardForm.svelte';
	import OrganizationSelect from '../components/form/OrganizationSelect.svelte';
	import UnitSelect from '../components/form/UnitSelect.svelte';
	import ContextSelect from '../components/form/ContextSelect.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isEmpty } from '../utils';
	import errors from "../errors";
	import { writable } from 'svelte/store';
	const validName = (name) => {
		return /^([a-z_]\d*(\.[a-z_])?)+$/.test(name) ? undefined : errors.NAMESPACE; //underscore should also be possible! see https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
	}
	const notEmpty = (value) => !!value ? undefined : errors.EMPTY;

	let namespace;
	let description;

	let compatibleUnits = writable([]);
	let compatibleContexts = writable([]);

	let fullyQualified;

	function changedOrganization(store) {
		$compatibleUnits = store ? $unitsStore.filter(u => u.organizationId === store.organizationId) : [];
		$unitStore = $compatibleUnits.length ? $compatibleUnits[$compatibleUnits.length-1] : undefined;
	}

	function changedUnit(store) {
		$compatibleContexts = store ? $contextsStore.filter(c => c.unitId === store.unitId) : [];
		$contextStore = $compatibleContexts.length ? $compatibleContexts[$compatibleContexts.length-1] : undefined;
	}

	function changedContext(store) {
		if(store) {
			namespace = store.namespace;
			description = store.description;
		} else {
			namespace = "";
			description = "";
		}
	}


	let defineMode = isEmpty(($contextsStore));
	const toggleDefineMode = () => {
		namespace = "";
		description = "";

		defineMode = !defineMode;
	}

	const define = async () => {
		if(!definable) return;
		SchemataRepository.createContext(($organizationStore).organizationId, ($unitStore).unitId, namespace, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}
	const redefine = async () => {
		if(!redefinable) return;
		SchemataRepository.updateContext(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, namespace, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}

	function updateStores(obj, reset = false) {
		console.log({obj});
		$contextStore = obj;
		if(reset) $contextsStore = ($contextsStore).filter(context => context.contextId != ($contextStore).contextId);
		$contextsStore = [...$contextsStore, obj];
	}
	function updateSelects() {
		// maybe also units..
		$compatibleContexts = $unitStore ? $contextsStore.filter(c => c.unitId == $unitStore.unitId) : [];
	}

	$: changedOrganization($organizationStore);
	$: changedUnit($unitStore);
	$: changedContext($contextStore);
	$: definable = namespace && description && $organizationStore && $unitStore;
	$: redefinable = definable && $contextStore;
	$: showNewButton = $contextsStore.length > 0;
</script>

<svelte:head>
	<title>Context</title>
</svelte:head>

<CardForm title="Context" linkToNext="New Schema" prevLink="unit" on:new={toggleDefineMode} on:redefine={redefine} on:define={define}
isDefineDisabled={!definable} isNextDisabled={defineMode} isRedefineDisabled={!redefinable}
{defineMode} {fullyQualified} {showNewButton}>
	<OrganizationSelect/>
	<UnitSelect {compatibleUnits}/>
	{#if !defineMode}
		<ContextSelect {compatibleContexts}/>
	{/if}
	<TextField class="mb-4 pb-4" placeholder="your.namespace.here" bind:value={namespace} rules={[notEmpty, validName]}>Namespace</TextField>
	<Textarea bind:value={description} rules={[notEmpty]}>Description</Textarea>
</CardForm>

<style>
	:global(.flex-child) {
		/* flex: 0 0 50%; */
		padding: 12px;
	}
</style>
