<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Button from '../components/Button.svelte';

	import marked from 'marked';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { contextStringReturner, getCompatible, getId, initSelected, isCompatibleToContext, isCompatibleToOrg, isCompatibleToUnit, orgStringReturner, schemaStringReturner, schemaVersionStringReturner, selectStringsFrom, unitStringReturner } from '../utils';
	import errors from '../errors';
	import ButtonBar from '../components/ButtonBar.svelte';
	
	const validator = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v)
	}

	let description;
	let previous = "0.0.0"; //previousVersion();
	let current = "0.0.1"; //= previous "+1"
	let specification;
	

	let compatibleUnits;
	let compatibleContexts;
	let compatibleSchemas;

	let selectedOrg = initSelected($organizationStore, orgStringReturner);
	$: organizationId = getId(selectedOrg);
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg);
		compatibleContexts = [];
		compatibleSchemas = [];

		console.log("in orgId");
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner);
	$: unitId = getId(selectedUnit);
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => isCompatibleToOrg(u));
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit);
			compatibleSchemas = [];
		}
		console.log("in unitId");
	}

	let selectedContext = initSelected($contextStore, contextStringReturner);
	$: contextId = getId(selectedContext);
	$: if(contextId || !contextId) {
		if(organizationId && unitId) {
			$contextStore = ($contextsStore).find(c => isCompatibleToUnit(c));
			compatibleSchemas = getCompatible($schemasStore, isCompatibleToContext, selectedContext);
		}
		console.log("in contextId");
	}


	let selectedSchema = initSelected($schemaStore, schemaStringReturner);
	$: schemaId = getId(selectedSchema);
	$: if(schemaId) $schemaStore = ($schemasStore).find(s => isCompatibleToContext(s));


	const orgSelect = selectStringsFrom($organizationsStore, orgStringReturner);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner);
	$: schemaSelect = selectStringsFrom(compatibleSchemas, schemaStringReturner);

	let clearFlag = false;
	const clear = () => {
		description = "";
		previous = "";
		current = "";
		specification = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner);
		selectedUnit = initSelected($unitStore, unitStringReturner);
		selectedContext = initSelected($contextStore, contextStringReturner);
		selectedSchema = initSelected($schemaStore, schemaStringReturner);

		clearFlag = !clearFlag;
	}

	const create = () => {
		if(!$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !description || !specification) {
			console.log(errors.SUBMIT);
			return;
		}
		if(!validator(previous) || !validator(current)) {
			console.log(errors.SUBMITVER);
			return;
		}
		SchemataRepository.createSchemaVersion(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId,
												($schemaStore).schemaId, specification, description, previous, current)
			.then(created => {
				console.log({created});
				$schemaVersionStore = created;
				$schemaVersionsStore.push(created);
				clear();
			})
	}

	let isCreateDisabled = true;
	let isNextDisabled = true;

	$: if(validator(previous) && validator(current) && description && specification && organizationId && unitId && contextId && schemaId) {
		isCreateDisabled = false;
	} else {
		isCreateDisabled = true;
	}

	$: if($schemaVersionStore) {
		isNextDisabled = false;
	}

</script>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:clear{clear} on:create={create}>
	<div class="flex-two-col">
		<ValidatedInput type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	</div>
	<div class="flex-two-col">
		<ValidatedInput type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
		<ValidatedInput type="select" label="Schema" bind:value={selectedSchema} {clearFlag} options={schemaSelect}/>
	</div>
	<div class="flex-two-col">
		<ValidatedInput label="Previous Version" bind:value={previous} {clearFlag} validator={validator}/>
		<ValidatedInput label="Current Version" bind:value={current} {clearFlag} validator={validator}/>
	</div>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
	<ValidatedInput type="textarea" label="Specification" bind:value={specification} {clearFlag}/>

	<div slot="buttons">
		<ButtonBar>
			<div class="mr-auto">
				<Button color="info" text="NEW" on:click={clear}/>
			</div>
			<Button color="primary" text="CREATE" on:click={create} disabled={isCreateDisabled}/>
			{#if !isNextDisabled}
				<Button color="primary" outline text={"Home"} href={"/"} disabled={isNextDisabled}/>
			{/if}
		</ButtonBar>
	</div>
</CardForm>

{#if description}
	{@html marked(description)}
{/if}

<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>