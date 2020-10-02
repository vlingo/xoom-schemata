<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isCompatibleToOrg, getCompatible, orgStringReturner, selectStringsFrom, unitStringReturner, initSelected, contextStringReturner, isCompatibleToUnit, isStoreEmpty, getFullyQualifiedName, contextIdReturner, unitIdReturner, orgIdReturner } from '../utils';
	import errors from "../errors";

	let namespace;
	let description;


	let compatibleUnits;
	let compatibleContexts;

	let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
	$: organizationId = selectedOrg.id;
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg.text);
		// clearing is necessary for grandchild
		compatibleContexts = [];

		fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
	$: unitId = selectedUnit.id;
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => u.unitId == unitId);
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit.text);

			fullyQualified = getFullyQualifiedName("unit", $unitStore);
		}
	}

	let selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);
	$: contextId = selectedContext.id;
	$: if(contextId) {
		$contextStore = ($contextsStore).find(c => c.contextId == contextId);

		fullyQualified = getFullyQualifiedName("context", $contextStore);
	}


	$: orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner, $detailed);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner, unitIdReturner, $detailed);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner, contextIdReturner, $detailed);


	let defineMode = isStoreEmpty(($contextsStore));
	let clearFlag = false;
	const newContext = () => {
		namespace = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
		selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (namespace && description && $organizationStore && $unitStore);
	const updatable = () => (namespace && description && $organizationStore && $unitStore && contextId);

	const define = async () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.createContext(($organizationStore).organizationId, ($unitStore).unitId, namespace, description)
			.then(created => {
				updateStores(created);
				updateSelects();
				defineMode = false;
			})
	}
	const save = async () => {
		if(!updatable()) { console.log(errors.SUBMIT); return; }
		SchemataRepository.updateContext(($organizationStore).organizationId, ($unitStore).unitId, contextId, namespace, description)
			.then(updated => {
				updateStores(updated, true);
				updateSelects();
			})
	}
	
	function updateStores(obj, reset = false) {
		console.log({obj});
		$contextStore = obj;
		if(reset) $contextsStore = ($contextsStore).filter(context => context.contextId != contextId);
		$contextsStore.push(obj);
	}
	function updateSelects() {
		// maybe also units..
		compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit.text);
		contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner, contextIdReturner, $detailed);
		selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(namespace && description && organizationId && unitId && defineMode) { //&& !contextId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(contextId) { isNextDisabled = false; }

	$: if(namespace && description && organizationId && unitId && contextId) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>

<CardForm title="Context" linkToNext="New Schema" on:new={newContext} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<!-- <div class="flex-two-col"> -->
		<ValidatedInput inline containerClasses="" type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput inline containerClasses="folder-inset1" type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	<!-- </div> -->
	{#if !defineMode}
		<ValidatedInput inline containerClasses="folder-inset2" disabled={defineMode} type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
	{/if}
	<ValidatedInput label="Namespace" bind:value={namespace} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>