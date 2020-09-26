<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Button from '../components/Button.svelte';

	import marked from 'marked';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { contextIdReturner, contextStringReturner, getCompatible, getFullyQualifiedName, getId, initSelected, isCompatibleToContext, isCompatibleToOrg, isCompatibleToUnit, isStoreEmpty, orgIdReturner, orgStringReturner, schemaIdReturner, schemaStringReturner, schemaVersionStringReturner, selectStringsFrom, unitIdReturner, unitStringReturner } from '../utils';
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

	let selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
	$: organizationId = selectedOrg.id;
	$: if(organizationId || !organizationId) {
		$organizationStore = ($organizationsStore).find(o => o.organizationId == organizationId);
		compatibleUnits = getCompatible($unitsStore, isCompatibleToOrg, selectedOrg.text);
		compatibleContexts = [];
		compatibleSchemas = [];
	}

	let selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
	$: unitId = selectedUnit.id;
	$: if(unitId || !unitId) {
		if(organizationId) {
			$unitStore = ($unitsStore).find(u => u.unitId == unitId);
			compatibleContexts = getCompatible($contextsStore, isCompatibleToUnit, selectedUnit.text);
			compatibleSchemas = [];
		}
	}

	let selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);
	$: contextId = selectedContext.id;
	$: if(contextId || !contextId) {
		if(organizationId && unitId) {
			$contextStore = ($contextsStore).find(c => c.contextId == contextId);
			compatibleSchemas = getCompatible($schemasStore, isCompatibleToContext, selectedContext.text);
		}
	}


	let selectedSchema = initSelected($schemaStore, schemaStringReturner, schemaIdReturner, $detailed);
	$: schemaId = selectedSchema.id;
	$: if(schemaId) $schemaStore = ($schemasStore).find(s => s.schemaId == schemaId);


	$: orgSelect = selectStringsFrom($organizationsStore, orgStringReturner, orgIdReturner, $detailed);
	$: unitSelect = selectStringsFrom(compatibleUnits, unitStringReturner, unitIdReturner, $detailed);
	$: contextSelect = selectStringsFrom(compatibleContexts, contextStringReturner, contextIdReturner, $detailed);
	$: schemaSelect = selectStringsFrom(compatibleSchemas, schemaStringReturner, schemaIdReturner, $detailed);


	let defineMode = isStoreEmpty(($schemaVersionsStore));
	let clearFlag = false;
	const newVersion = () => {
		// description = "";
		previous = "0.0.0";
		current = "0.0.1";
		// specification = "";
		selectedOrg = initSelected($organizationStore, orgStringReturner, orgIdReturner, $detailed);
		selectedUnit = initSelected($unitStore, unitStringReturner, unitIdReturner, $detailed);
		selectedContext = initSelected($contextStore, contextStringReturner, contextIdReturner, $detailed);
		selectedSchema = initSelected($schemaStore, schemaStringReturner, schemaIdReturner, $detailed);

		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (specification && description && $organizationStore && $unitStore && $contextStore && $schemaStore);

	const define = () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		if(!validator(previous) || !validator(current)) { console.log(errors.SUBMITVER); return; }
		SchemataRepository.createSchemaVersion(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId,
												($schemaStore).schemaId, specification, description, previous, current)
			.then(created => {
				updateStores(created);
				defineMode = false;
			})
	}

	function updateStores(obj) {
		console.log({obj});
		$schemaVersionStore = obj;
		$schemaVersionsStore.push(obj);
	}

	let isCreateDisabled = true;
	let isNextDisabled = true;

	$: if(validator(previous) && validator(current) && description && specification && organizationId && unitId && contextId && schemaId && defineMode) {
		isCreateDisabled = false;
	} else {
		isCreateDisabled = true;
	}

	$: if($schemaVersionStore) { isNextDisabled = false; }

	let fullyQualified;
</script>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:new={newVersion} on:define={define} {defineMode} {fullyQualified}>
	<!-- <div class="flex-two-col"> -->
		<ValidatedInput inline containerClasses="" type="select" label="Organization" bind:value={selectedOrg} {clearFlag} options={orgSelect}/>
		<ValidatedInput inline containerClasses="folder-inset1" type="select" label="Unit" bind:value={selectedUnit} {clearFlag} options={unitSelect}/>
	<!-- </div> -->
	<!-- <div class="flex-two-col"> -->
		<ValidatedInput inline containerClasses="folder-inset2" type="select" label="Context" bind:value={selectedContext} {clearFlag} options={contextSelect}/>
		<ValidatedInput inline containerClasses="folder-inset3" type="select" label="Schema" bind:value={selectedSchema} {clearFlag} options={schemaSelect}/>
	<!-- </div> -->
	<div class="flex-two-col">
		<ValidatedInput label="Previous Version" bind:value={previous} {clearFlag} validator={validator}/>
		<ValidatedInput label="Current Version" bind:value={current} {clearFlag} validator={validator}/>
	</div>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag}/>
	<ValidatedInput type="textarea" label="Specification" bind:value={specification} {clearFlag} rows="6"/>

	<div slot="buttons">
		<ButtonBar>
			<div class="mr-auto">
				<Button color="info" text="New Schema Version" on:click={newVersion}/>
			</div>
			{#if defineMode}
				<Button color="primary" text="Define" on:click={define} disabled={isCreateDisabled}/>
			{/if}
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