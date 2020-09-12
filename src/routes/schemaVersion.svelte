<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import marked from 'marked';
import Button from '../components/Button.svelte';

	let id;
	let name;
	let description = ``;

	let organizations = ["1", "2", "3"];
	let organization;

	let units = ["1", "2", "3"];
	let unit;

	let contexts = ["1", "2", "3"];
	let context;

	let schemas = ["1", "2", "3"];
	let schema;

	let previous;
	let current;

	let specification;

	let clearFlag = false;

	const clear = () => {
		id = "";
		name = "";
		description = "";
		organization = "";
		unit = "";
		context = "";
		schema = "";
		previous = "";
		current = "";
		specification = "";

		clearFlag = !clearFlag;
	}

	const update = () => {
		updateUnit(id, name, description, organization);
	}

	const create = () => {
		//id gets generated
		createUnit(name, description, organization);
	}

	const validator = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v)
	}
</script>

<CardForm title="Schema Version" next="" on:clear{clear} on:update on:create>
	<ValidatedInput label="SchemaID" bind:value={id} disabled/>
	<div class="flex-two-col">
		<ValidatedInput type="select" label="Organization" bind:value={organization} {clearFlag} options={organizations}/>
		<ValidatedInput type="select" label="Unit" bind:value={unit} {clearFlag} options={units}/>
	</div>
	<div class="flex-two-col">
		<ValidatedInput type="select" label="Context" bind:value={context} {clearFlag} options={contexts}/>
		<ValidatedInput type="select" label="Schema" bind:value={schema} {clearFlag} options={schemas}/>
	</div>
	<div class="flex-two-col">
		<ValidatedInput label="Previous Version" bind:value={previous} {clearFlag} validator={validator}/>
		<ValidatedInput label="Current Version" bind:value={current} {clearFlag} validator={validator}/>
	</div>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
	<ValidatedInput type="textarea" label="Specification" bind:value={specification} {clearFlag}/>

	<!-- <div slot="buttons">
		<Button color="primary" text="CREATE" on:click={() => {}}/>
	</div> -->
</CardForm>
{@html marked(description)}

<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>