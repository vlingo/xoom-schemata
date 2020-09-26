<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';

	import SchemataRepository from '../api/SchemataRepository';
	import { organizationStore, organizationsStore } from '../stores'
	import errors from '../errors';
	import { getFullyQualifiedName, initSelected, isStoreEmpty, orgIdReturner, orgStringReturner, selectStringsFrom } from '../utils';

	let name;
	let description;

	let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner);
	$: organizationId = selectedOrg.id;
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);

		fullyQualified = getFullyQualifiedName("unit", $organizationStore);
	} 

	let orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner);

	const define = async () => {
		// could also be implemented by firing validation on the inputs (via flag or exposing valueValid/Invalid) <-no, just disable buttons again, and this is an extra
		if(!name || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.createOrganization(name, description)
			.then(created => {
				console.log({created});
				$organizationStore = created;
				$organizationsStore.push(created);

				orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner);
				selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner);
			})
		isNextDisabled = false;
		defineMode = false;
	}

	const save = async () => {
		if(!organizationId || !name || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.updateOrganization(organizationId, name, description)
			.then(updated => {
				console.log({updated});
				$organizationStore = updated;
				$organizationsStore = ($organizationsStore).filter(org => org.organizationId != organizationId);
				$organizationsStore.push(updated);
				
				orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner);
				selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner);
			})
	}
	
	let defineMode = isStoreEmpty(($organizationsStore)); //maybe this doesn't work anymore, maybe .length,...
	// maybe look into this, I just don't know enough stuff - document.getElementById("myForm").reset();
	let clearFlag = false;
	const newOrg = () => {
		name = "";
		description = "";
		
		defineMode = true;
		clearFlag = !clearFlag;
	}

	let isDefineDisabled = true;
	let isNextDisabled = true;
	let isSaveDisabled = true;

	$: if(name && description && defineMode) { //&& !orgId
		isDefineDisabled = false;
	} else {
		isDefineDisabled = true;
	}

	$: if(organizationId) {
		isNextDisabled = false;
	}
	
	$: if(organizationId && name && description) {
		isSaveDisabled = false;
	} else {
		isSaveDisabled = true;
	}

	let fullyQualified;
</script>


<CardForm title="Organization" linkToNext="New Unit" on:new={newOrg} on:save={save} on:define={define} 
{isDefineDisabled} {isNextDisabled} {isSaveDisabled} {defineMode} {fullyQualified}>
	{#if !defineMode}
		<ValidatedInput inline containerClasses="" disabled={defineMode} type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
	{/if}
	<ValidatedInput label="Name" bind:value={name} {clearFlag}/>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
</CardForm>