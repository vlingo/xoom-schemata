<script>
import SchemataRepository from '../api/SchemataRepository';

	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
import { contextsStore, contextStore, organizationStore, schemasStore, schemaStore, unitStore } from '../stores';

	let id;
	let name;
	let description;
	let categories = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category;
	let scopes = ["Private", "Public"];
	let scope;

	let compatibleUnits;

	// let selectedOrg = $organizationStore? orgStringReturner(($organizationStore)) : ""; //initial value
	// $: organizationId = getId(selectedOrg); //last index should always be the id!
	// $: if(organizationId || !organizationId) {
	// 	$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
	// 	compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
	// }

	// let selectedUnit = $unitStore? unitStringReturner(($unitStore)) : ""; //initial, should always be compatible, because you need to choose org first.
	// $: unitId = getId(selectedUnit);
	// $: if(unitId) $unitStore = ($unitsStore).find(u => (u.unitId == unitId) && (u.organizationId == organizationId));


	// //strings which are shown to the user, unitSelect changes if compatibleUnits change
	// const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	// $: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);

	let organizations = ["1", "2", "3"];
	let organization;

	let units = ["1", "2", "3"];
	let unit;

	let contexts = ["1", "2", "3"];
	let context;

	let clearFlag = false;
	const clear = () => {
		id = "";
		name = "";
		description = "";
		category = "";
		scope = "";

		clearFlag = !clearFlag;
	}

	const create = () => {
		if(!namespace || !description || !$organizationStore || !$unitStore) {
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

<CardForm title="Schema" next="SCHEMA VERSION" on:clear={clear} on:update on:create>
	<ValidatedInput label="SchemaID" bind:value={id} disabled/>
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Organization" bind:value={organization} {clearFlag} options={organizations}/>
		<ValidatedInput type="select" label="Unit" bind:value={unit} {clearFlag} options={units}/>
	</span>
	<ValidatedInput type="select" label="Context" bind:value={context} {clearFlag} options={contexts}/>
	<span class="flex-two-col">
		<ValidatedInput type="select" label="Category" bind:value={category} {clearFlag} options={categories}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} {clearFlag} options={scopes}/>
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