<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore } from '../stores';
	import { initSelected, orgStringReturner, selectStringsFrom } from '../utils';
	import errors from '../errors';

	let id;
	let name;
	let description;

	// $: $organizationStore = changedOrg(orgId);
	// function changedOrg(orgId) {
	// 	return ($organizationsStore).find(o => o.name == orgId);
	// }

	let selectedOrg = initSelected($organizationStore, orgStringReturner); //initial value
	$: orgId = selectedOrg.split(" ")[selectedOrg.split(" ").length-1]; //last index should always be the id!
	$: if(orgId || !orgId) $organizationStore = ($organizationsStore).find(o => o.organizationId == orgId);

	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	
	let clearFlag = false;
	const clear = () => {
		id = "";
		name = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);

		clearFlag = !clearFlag;
	}

	const create = async () => {
		if(!name || !description || !$organizationStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createUnit(($organizationStore).organizationId, name, description)
			.then(created => {
				console.log({created});
				$unitStore = created;
				$unitsStore.push(created);
				clear();
			})
	}
</script>

<CardForm title="Unit" linkToNext="CREATE CONTEXT" on:clear={clear} on:update on:create={create}>
	<ValidatedInput label="UnitID" bind:value={id} disabled/>
	<ValidatedInput type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>

<!-- <CardForm title="Unit" next="CONTEXT" on:clear={clear} on:update on:create>
	<FormGroup>
		<Label for="unitId">UnitID</Label>
		<Input type="text" name="unitId" id="unitId" placeholder="" bind:value={id} disabled/>
	</FormGroup>
	<FormGroup>
		<Label for="organization">Organization</Label>
		<Input type="select" name="organization" id="organization" placeholder=""
		bind:value={organization} valid={organizationValid} invalid={organizationInvalid}
		on:change={organizationCheck} on:blur={organizationCheck}>
			<option/>
			{#each organizations as organization}
				<option>{organization}</option>
			{/each}
		</Input>
	</FormGroup>
	<FormGroup>
		<Label for="name">Name</Label>
		<Input type="text" name="name" id="name" placeholder="Name"
		bind:value={name} valid={nameValid} invalid={nameInvalid}
		on:blur={nameCheck} on:keyup={nameCheck}/>
	</FormGroup>
	<FormGroup>
		<Label for="description">Description</Label>
		<Input type="textarea" name="description" id="description" placeholder="Description"
		bind:value={description} valid={descriptionValid} invalid={descriptionInvalid}
		on:blur={descriptionCheck} on:keyup={descriptionCheck}/>
	</FormGroup>
</CardForm> -->