<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Select from '../components/form/Select.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from "../errors";
	const validName = (name) => {
		return /^([a-z_]\d*(\.[a-z_])?)+$/.test(name) ? undefined : errors.NAMESPACE; //underscore should also be possible! see https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
	}

	let namespace;
	let description;

	let compatibleUnits = [];
	let compatibleContexts = [];

	$: changedOrganization($organizationStore)
	function changedOrganization(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	$: changedUnit($unitStore)
	function changedUnit(store) {
		compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = compatibleContexts.length > 0 ? compatibleContexts[compatibleContexts.length-1] : undefined;
	}
	$: changedContext($contextStore);
	function changedContext(store) {
		if(store) {
			namespace = store.namespace;
			description = store.description;
		} else {
			namespace = "";
			description = "";
		}
	}


	let defineMode = isStoreEmpty(($contextsStore));
	const newContext = () => {
		namespace = "";
		description = "";

		defineMode = true;
	}

	const definable = () => (namespace && description && $organizationStore && $unitStore);
	const updatable = () => (namespace && description && $organizationStore && $unitStore && $contextStore);

	const define = async () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createContext(($organizationStore).organizationId, ($unitStore).unitId, namespace, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}
	const redefine = async () => {
		if(!updatable()) { console.log(errors.SUBMIT); return; }
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
		$contextsStore.push(obj);
	}
	function updateSelects() {
		// maybe also units..
		compatibleContexts = $unitStore ? $contextsStore.filter(c => c.unitId == $unitStore.unitId) : [];
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isRedefineDisabled = true;

	$: if(!validName(namespace) && description && $organizationStore && $unitStore && defineMode) {
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(!defineMode) { isNextDisabled = false; }

	$: if(!validName(namespace) && namespace && description && $organizationStore && $unitStore && $contextStore) {
		isRedefineDisabled = false;
	} else {
		isRedefineDisabled = true;
	}

	let fullyQualified;
</script>

<svelte:head>
	<title>Context</title>
</svelte:head>

<CardForm title="Context" linkToNext="New Schema" on:new={newContext} on:redefine={redefine} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isRedefineDisabled} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	{#if !defineMode}
		<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	{/if}

	<ValidatedInput label="Namespace" placeholder="your.namespace.here" bind:value={namespace} validator={validName} invalidString={errors.NAMESPACE}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description}/>
</CardForm>