<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, unitsStore, unitStore } from '../stores';
	import { isCompatibleToOrg, getCompatible, getId, orgStringReturner, selectStringsFrom, unitStringReturner, initSelected } from '../utils';
	import errors from "../errors";

	let id;
	let namespace;
	let description;

	let compatibleUnits;

	let selectedOrg = initSelected($organizationStore, orgStringReturner);
	$: organizationId = getId(selectedOrg); //last index should always be the id!
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner); //initial, should always be compatible, because you need to choose org first.
	$: unitId = getId(selectedUnit);
	$: if(unitId) $unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));


	//strings which are shown to the user, unitSelect changes if compatibleUnits change
	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);

	
	let clearFlag = false;
	const clear = () => {
		id = "";
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
			})
	}

</script>

<CardForm title="Context" linkToNext="CREATE SCHEMA" on:clear={clear} on:update on:create={create}>
	<ValidatedInput label="ContextID" bind:value={id} disabled/>
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