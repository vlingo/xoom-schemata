<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
import errors from '../errors';

	let id;
	let name;
	let description;
	// let nameValid = false;
	// let nameInvalid = false;
	// let descriptionValid = false;
	// let descriptionInvalid = false;

	// const nameCheck = () => {
	// 	if(name) {
	// 		nameValid = true;
	// 		nameInvalid = false;
	// 	} else {
	// 		nameValid = false;
	// 		nameInvalid = true;
	// 	}
	// }

	// const descriptionCheck = () => {
	// 	if(description) {
	// 		descriptionValid = true;
	// 		descriptionInvalid = false;
	// 	} else {
	// 		descriptionValid = false;
	// 		descriptionInvalid = true;
	// 	}
	// }

	// function valueChanged(value, valueValid, valueInvalid) {
	// 	console.log(value, valueValid, valueInvalid);
	// 	if(value) {
	// 		valueValid = true;
	// 		valueInvalid = false;
	// 	} else {
	// 		valueValid = false;
	// 		valueInvalid = true;
	// 	}
	// 	console.log(name, nameValid, nameInvalid);
	// }

	// function valueChanged(object, e) {
	// 	console.log("before change", object);
	// 	object.value = e.target.value;
	// 	console.log("value changed", object);
	// 	if(object.value) {
	// 		object.valid = true;
	// 		object.invalid = false;
	// 	} else {
	// 		object.valid = false;
	// 		object.invalid = true;
	// 	}
	// 	console.log("valid changed", object);
	// 	console.log(name, nameValid, nameInvalid);
	// }

	const create = async () => {
		// could also be implemented by firing validation on the inputs (via flag or exposing valueValid/Invalid)
		if(!name || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createOrganization(name, description)
			.then(created => {
				console.log({created});
				$organizationStore = created;
				$organizationsStore.push(created);
				clear();
			})
	}
	

	let clearFlag = false;
	const clear = () => {
		id = "";
		name = "";
		description = "";

		clearFlag = !clearFlag;
	}

</script>





<CardForm title="Organization" next="UNIT" on:clear={clear} on:update on:create={create}>
	<ValidatedInput label="OrganizationID" bind:value={id} disabled/>
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>


<!-- <Card>
	<CardHeader tag="h3">Organization</CardHeader>
	<CardBody>
		<Form>
			<FormGroup>
				<Label for="organizationId">OrganizationID</Label>
				<Input type="text" name="organizationId" id="organizationId" placeholder="" bind:value={id} disabled/>
			</FormGroup>
			<FormGroup>
				<Label for="name">Name</Label>
				<Input type="text" name="name" id="name" placeholder="Name"
				bind:value={name} valid={nameValid} invalid={nameInvalid}
				on:blur={nameCheck} on:keyup={nameCheck}/>
				<!-- on:change={nameCheck} --><!--
			</FormGroup>
			<FormGroup>
				<Label for="description">Description</Label>
				<Input type="textarea" name="description" id="description" placeholder="Description"
				bind:value={description} valid={descriptionValid} invalid={descriptionInvalid}
				on:blur={descriptionCheck} on:keyup={descriptionCheck}/>
				<!-- on:change={descriptionCheck} --><!--
				<!-- make github pull request: <textarea> should have placeholder too --><!--
				<!-- make github pull request: <Input> readonly should be false by default (browser warnings) --><!--
			</FormGroup>
		</Form>
		<Button color="primary" outline on:click={clear}>new (clear)</Button>
		<Button color="primary" outline on:click={update}>save (update)</Button>
		<Button color="primary" outline on:click={create}>create</Button>
		<Button color="primary" outline href="unit">create unit</Button>
	</CardBody>
</Card> -->