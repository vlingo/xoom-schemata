<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';
	import { contextStringReturner, getCompatible, getId, initSelected, isCompatibleToOrg, isCompatibleToUnit, orgStringReturner, selectStringsFrom, unitStringReturner } from '../utils';
import errors from '../errors';

	let id;
	let name;
	let description;
	let categorySelect = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category;
	let scopeSelect = ["Private", "Public"];
	let scope;

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

	let clearFlag = false;
	const clear = () => {
		id = "";
		name = "";
		description = "";
		category = "";
		scope = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);
		selectedUnit = initSelected($unitStore, unitStringReturner);
		selectedContext = initSelected($contextStore, contextStringReturner);

		clearFlag = !clearFlag;
	}

	const create = () => {
		if(!name || !description || !$organizationStore || !$unitStore || !$contextStore || !scope || !category) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createSchema(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, name, scope, category, description)
			.then(created => {
				console.log({created});
				$schemaStore = created;
				$schemasStore.push(created);
				clear();
			})
	}

</script>

<CardForm title="Schema" linkToNext="SCHEMA VERSION" href="schemaVersion" on:clear={clear} on:update on:create={create}>
	<ValidatedInput label="SchemaID" bind:value={id} disabled/>
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	</span>
	<ValidatedInput type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Category" bind:value={category} {clearFlag} options={categorySelect}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} {clearFlag} options={scopeSelect}/>
	</span>
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>


<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>