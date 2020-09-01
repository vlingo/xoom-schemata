<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import marked from 'marked';

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

</script>

<CardForm title="Schema Version" next="schemaVersion" on:clear={clear} on:update on:create>
	<ValidatedInput label="SchemaID" bind:value={id} disabled/>
	<ValidatedInput type="select" label="Organization" bind:value={organization} clear={clearFlag} options={organizations}/>
	<ValidatedInput type="select" label="Unit" bind:value={unit} clear={clearFlag} options={units}/>
	<ValidatedInput type="select" label="Context" bind:value={context} clear={clearFlag} options={contexts}/>
	<ValidatedInput type="select" label="Schema" bind:value={schema} clear={clearFlag} options={schemas}/>
	<ValidatedInput label="Previous Version" bind:value={previous} clear={clearFlag}/>
	<ValidatedInput label="Current Version" bind:value={current} clear={clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} clear={clearFlag}/>
	<ValidatedInput type="textarea" label="Specification" bind:value={specification} clear={clearFlag}/>
	
</CardForm>
{@html marked(description)}