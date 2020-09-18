<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isCompatibleToOrg, getCompatible, getId, orgStringReturner, selectStringsFrom, unitStringReturner, initSelected, contextStringReturner, isCompatibleToUnit } from '../utils';
	import errors from "../errors";

	let namespace;
	let description;


	let contextSelectDisabled = ($contextsStore).length < 1;
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

	
	let clearFlag = false;
	const clear = () => {
		namespace = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);
		selectedUnit = initSelected($unitStore, unitStringReturner);

		clearFlag = !clearFlag;
	}

	const create = async () => {
		if(!namespace || !description || !$organizationStore || !$unitStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createContext(($organizationStore).organizationId, ($unitStore).unitId, namespace, description)
			.then(created => {
				console.log({created});
				$contextStore = created;
				$contextsStore.push(created);
				clear();
				contextSelect = selectStringsFrom($contextsStore, contextStringReturner);
				selectedContext = initSelected($contextStore, contextStringReturner);
				contextSelectDisabled = false;
			})
	}

	const update = async () => {
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
				clear();
				contextSelect = selectStringsFrom($contextsStore, contextStringReturner);
				selectedContext = initSelected($contextStore, contextStringReturner);
			})
	}

	let isCreateDisabled = true;
	let isNextDisabled = true;
	let isUpdateDisabled = true;

	$: if(namespace && description && organizationId && unitId) { //&& !contextId
		isCreateDisabled = false;
	} else {
		isCreateDisabled = true;
	}

	$: if(contextId) {
		isNextDisabled = false;
	}

	$: if(namespace && description && organizationId && unitId && contextId) {
		isUpdateDisabled = false;
	} else {
		isUpdateDisabled = true;
	}
</script>

<CardForm title="Context" linkToNext="CREATE SCHEMA" on:clear={clear} on:update={update} on:create={create} {isCreateDisabled} {isNextDisabled} {isUpdateDisabled}>
	<!-- extra-component only shown in update mode ? -->
		<ValidatedInput disabled={contextSelectDisabled} type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
	<!-- /<ValidatedInput label="ContextID" bind:value={id} disabled/> -->
	<div class="flex-two-col">
		<ValidatedInput type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	</div>
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