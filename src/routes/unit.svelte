<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationsStore, organizationStore, unitStore, unitsStore } from '../stores';
	import { getCompatible, getFullyQualifiedName, getId, initSelected, isCompatibleToOrg, isStoreEmpty, orgStringReturner, selectStringsFrom, unitStringReturner } from '../utils';
	import errors from '../errors';

	let name;
	let description;


	// could change to that, but more to write
	// $: $organizationStore = changedOrg(orgId);
	// function changedOrg(orgId) {
	// 	return ($organizationsStore).find(o => o.name == orgId);
	// }

	let compatibleUnits;

	let selectedOrg = initSelected($organizationStore, orgStringReturner);
	$: organizationId = getId(selectedOrg); //last index should always be the id!
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);

		fullyQualified = getFullyQualifiedName("organization", $organizationStore);
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner); //initial, should always be compatible, because you need to choose org first.
	$: unitId = getId(selectedUnit);
	$: if(unitId) {
		$unitStore = ($unitsStore).find(u => u.unitId == unitId);

		fullyQualified = getFullyQualifiedName("unit", $unitStore);
	}


	//strings which are shown to the user, unitSelect changes if compatibleUnits change
	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);



	// let selectedOrg = initSelected($organizationStore, orgStringReturner); //initial value
	// $: orgId = getId(selectedOrg); //last index should always be the id!
	// $: if(orgId || !orgId) $organizationStore = ($organizationsStore).find(o => o.organizationId == orgId);

	// const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	let defineMode = isStoreEmpty(($unitsStore));
	let clearFlag = false;
	const newUnit = () => {
		name = "";
		description = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const define = async () => {
		if(!name || !description || !$organizationStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createUnit(($organizationStore).organizationId, name, description)
			.then(created => {
				console.log({created});
				$unitStore = created;
				$unitsStore.push(created);
				
				unitSelect = selectStringsFrom($unitsStore, unitStringReturner);
				selectedUnit = initSelected($unitStore, unitStringReturner);
			})
		defineMode = false;
	}

	// maybe the unitId should also come from the store (if we validate the value before pushing it to the store, for example.)
	// can just be changed here and work the same
	const save = async () => {
		if(!unitId || !name || !description || !$organizationStore) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.updateUnit(($organizationStore).organizationId, unitId, name, description)
			.then(updated => {
				console.log({updated});
				$unitStore = updated;
				$unitsStore = ($unitsStore).filter(unit => unit.unitId != unitId);
				$unitsStore.push(updated);
				
				unitSelect = selectStringsFrom($unitsStore, unitStringReturner);
				selectedUnit = initSelected($unitStore, unitStringReturner);
			})
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && organizationId && defineMode) { //&& !unitId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(unitId) {
		isNextDisabled = false;
	}

	$: if(unitId && organizationId && name && description) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>

<CardForm title="Unit" linkToNext="New Context" on:new={newUnit} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	<ValidatedInput inline containerClasses="" type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
	{#if !defineMode}
		<ValidatedInput inline containerClasses="folder-inset1" disabled={defineMode} type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	{/if}
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