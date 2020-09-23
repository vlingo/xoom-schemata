<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isCompatibleToOrg, getCompatible, getId, orgStringReturner, selectStringsFrom, unitStringReturner, initSelected, contextStringReturner, isCompatibleToUnit, isStoreEmpty } from '../utils';
	import errors from "../errors";

	let namespace;
	let description;


	let compatibleUnits;
	let compatibleContexts;

	let selectedOrg = initSelected($organizationStore, orgStringReturner);
	$: organizationId = getId(selectedOrg);
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
		// clearing is necessary for grandchild
		compatibleContexts = [];

		console.log("in orgId");
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner);
	$: unitId = getId(selectedUnit);
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit);
		}
		console.log("in unitId");
	}

	let selectedContext = initSelected($contextStore, contextStringReturner);
	$: contextId = getId(selectedContext);
	$: if(contextId) $contextStore = ($contextsStore).find(c => isCompatibleToUnit(c));


	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner);

	

	// let compatibleUnits;

	// let selectedOrg = initSelected($organizationStore, orgStringReturner);
	// $: organizationId = getId(selectedOrg); //last index should always be the id!
	// $: if(organizationId || !organizationId) {
	// 	$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
	// 	compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
	// }

	// let selectedUnit = initSelected($unitStore, unitStringReturner); //initial, should always be compatible, because you need to choose org first.
	// $: unitId = getId(selectedUnit);
	// $: if(unitId) $unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));


	// //strings which are shown to the user, unitSelect changes if compatibleUnits change
	// const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	// $: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);

	let defineMode = isStoreEmpty(($contextsStore));
	let clearFlag = false;
	const newContext = () => {
		namespace = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);
		selectedUnit = initSelected($unitStore, unitStringReturner);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const define = async () => {
		if(!namespace || !description || !$organizationStore || !$unitStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createContext(($organizationStore).organizationId, ($unitStore).unitId, namespace, description)
			.then(created => {
				console.log({created});
				$contextStore = created;
				$contextsStore.push(created);
				
				contextSelect = selectStringsFrom($contextsStore, contextStringReturner);
				selectedContext = initSelected($contextStore, contextStringReturner);
			})
		defineMode = false;
	}

	const save = async () => {
		if(!contextId || !namespace || !description || !$organizationStore || !$unitStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.updateContext(($organizationStore).organizationId, ($unitStore).unitId, contextId, namespace, description)
			.then(updated => {
				console.log({updated});
				$contextStore = updated;
				$contextsStore = ($contextsStore).filter(context => context.contextId != contextId);
				$contextsStore.push(updated);
				
				contextSelect = selectStringsFrom($contextsStore, contextStringReturner);
				selectedContext = initSelected($contextStore, contextStringReturner);
			})
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(namespace && description && organizationId && unitId && defineMode) { //&& !contextId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(contextId) {
		isNextDisabled = false;
	}

	$: if(namespace && description && organizationId && unitId && contextId) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}
</script>

<CardForm title="Context" linkToNext="New Schema" on:new={newContext} on:save={save} on:define={define} {isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode}>
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

<style>
	.flex-two-col {
		display: flex;
		/* flex-basis: 50%; */
		flex-wrap: wrap;
	}
</style>