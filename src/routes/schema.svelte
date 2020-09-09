<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	let id;
	let name;
	let description;

	let organizations = ["1", "2", "3"];
	let organization;

	let units = ["1", "2", "3"];
	let unit;

	let contexts = ["1", "2", "3"];
	let context;

	let categories = ["Command", "Data", "Document", "Envelope", "Event", "Unknown"];
	let category;

	let scopes = ["Private", "Public"];
	let scope;

	let clearFlag = false;

	const clear = () => {
		id = "";
		name = "";
		description = "";
		organization = "";
		unit = "";
		context = "";
		category = "";
		scope = "";

		clearFlag = !clearFlag;
	}

	const update = () => {
		updateUnit(id, name, description, organization);
	}

	const create = () => {
		//id gets generated
		createUnit(name, description, organization);
	}

</script>

<CardForm title="Schema" next="SCHEMA VERSION" on:clear={clear} on:update on:create>
	<ValidatedInput label="SchemaID" bind:value={id} disabled/>
	<div class="flex">
		<ValidatedInput type="select" label="Organization" bind:value={organization} clear={clearFlag} options={organizations}/>
		<ValidatedInput type="select" label="Unit" bind:value={unit} clear={clearFlag} options={units}/>
	</div>
	<ValidatedInput type="select" label="Context" bind:value={context} clear={clearFlag} options={contexts}/>
	<div class="flex">
		<ValidatedInput type="select" label="Category" bind:value={category} clear={clearFlag} options={categories}/>
		<ValidatedInput type="select" label="Scope" bind:value={scope} clear={clearFlag} options={scopes}/>
	</div>
	<ValidatedInput label="Name" bind:value={name} clear={clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} clear={clearFlag}/>
</CardForm>