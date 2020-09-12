<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import errors from '../errors';

	let id;
	let name;
	let description;

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