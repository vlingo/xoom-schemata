<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import CardForm from '../components/CardForm.svelte';

	let id;
	let name;
	let description;
	let nameValid = false;
	let nameInvalid = false;
	let descriptionValid = false;
	let descriptionInvalid = false;

	let organizations = ["1", "2", "3"]; //["1", "2", "3"];
	let organization;
	let organizationValid = false;
	let organizationInvalid = false;

	const clear = () => {
		id = "";
		name = "";
		description = "";
		organization = "";
		nameValid = false;
		nameInvalid = false;
		descriptionValid = false;
		descriptionInvalid = false;
		organizationValid = false;
		organizationInvalid = false;
	}

	const update = () => {
		updateUnit(id, name, description, organization);
	}

	const create = () => {
		//id gets generated
		createUnit(name, description, organization);
	}

	const nameCheck = () => {
		if(name) {
			nameValid = true;
			nameInvalid = false;
		} else {
			nameValid = false;
			nameInvalid = true;
		}
	}

	const descriptionCheck = () => {
		if(description) {
			descriptionValid = true;
			descriptionInvalid = false;
		} else {
			descriptionValid = false;
			descriptionInvalid = true;
		}
	}

	const organizationCheck = (e) => {
		organization = e.target.value;

		if(organization) {
			organizationValid = true;
			organizationInvalid = false;
		} else {
			organizationValid = false;
			organizationInvalid = true;
		}
	}

</script>

<CardForm title="Unit" next="CONTEXT" on:clear={clear} on:update on:create>
	<FormGroup>
		<Label for="unitId">UnitID</Label>
		<Input type="text" name="unitId" id="unitId" placeholder="" bind:value={id} disabled/>
	</FormGroup>
	<FormGroup>
		<Label for="organization">Organization</Label>
		<Input type="select" name="organization" id="organization" placeholder=""
		bind:value={organization} valid={organizationValid} invalid={organizationInvalid}
		on:change={organizationCheck} on:blur={organizationCheck}>
		<!-- on:blur={organizationCheck} on:keyup={organizationCheck}
		//before, it worked like the others (but works only with first option being empty) -->
			<option/>
			{#each organizations as organization}
				<option>{organization}</option>
			<!-- {:else}
				<option>none</option> -->
			{/each}
		</Input>
	</FormGroup>
	<FormGroup>
		<Label for="name">Name</Label>
		<Input type="text" name="name" id="name" placeholder="Name"
		bind:value={name} valid={nameValid} invalid={nameInvalid}
		on:blur={nameCheck} on:keyup={nameCheck}/>
		<!-- on:change={nameCheck} -->
	</FormGroup>
	<FormGroup>
		<Label for="description">Description</Label>
		<Input type="textarea" name="description" id="description" placeholder="Description"
		bind:value={description} valid={descriptionValid} invalid={descriptionInvalid}
		on:blur={descriptionCheck} on:keyup={descriptionCheck}/>
		<!-- on:change={descriptionCheck} -->
		<!-- make github pull request: <textarea> should have placeholder too -->
	</FormGroup>
</CardForm>