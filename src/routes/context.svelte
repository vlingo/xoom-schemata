<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	let id;
	let name;
	let description;

	let organizations = ["1", "2", "3"];
	let organization;

	let units = ["1", "2", "3"];
	let unit;

	let clearFlag = false;

	const clear = () => {
		id = "";
		name = "";
		description = "";
		organization = "";
		unit = "";

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

<CardForm title="Context" next="schema" on:clear={clear} on:update on:create>
	<ValidatedInput label="ContextID" bind:value={id} disabled/>
	<ValidatedInput type="select" label="Organization" bind:value={organization} clear={clearFlag} options={organizations}/>
	<ValidatedInput type="select" label="Unit" bind:value={unit} clear={clearFlag} options={units}/>
	<ValidatedInput label="Name" bind:value={name} clear={clearFlag}/>
	<ValidatedInput label="Description" bind:value={description} clear={clearFlag}/>
</CardForm>