<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Button from '../components/form/Button.svelte';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import Select from '../components/form/Select.svelte';

	import marked from 'marked';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';
import Diff from '../components/Diff.svelte';
	const validator = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v)
	}

	let description = $schemaVersionStore? $schemaVersionStore.description : "";
	let previous = $schemaVersionStore? $schemaVersionStore.previousVersion : "0.0.0";
	let current = $schemaVersionStore? $schemaVersionStore.currentVersion : "0.0.1";
	let specification = $schemaVersionStore? $schemaVersionStore.specification : "";

	let compatibleUnits = [];
	let compatibleContexts = [];
	let compatibleSchemas = [];
	let compatibleVersions = [];
	$: changedUnits($organizationStore)
	function changedUnits(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	$: changedContexts($unitStore)
	function changedContexts(store) {
		compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = compatibleContexts.length > 0 ? compatibleContexts[compatibleContexts.length-1] : undefined;
	}
	$: changedSchemas($contextStore);
	function changedSchemas(store) {
		compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = compatibleSchemas.length > 0 ? compatibleSchemas[compatibleSchemas.length-1] : undefined;
	}
	$: changedVersions($schemaStore);
	function changedVersions(store) {
		compatibleVersions = store ? $schemaVersionsStore.filter(v => v.schemaId == store.schemaId) : [];
		$schemaVersionStore = store ? $schemaVersionsStore.find(v => v.schemaId == store.schemaId) : undefined;

		store ? specification = `${store.category.toLowerCase()} ${store.name} {\n\t\n}` : "";
		if($schemaVersionStore) {
			description = $schemaVersionStore.description;
			specification = $schemaVersionStore.specification;
			defineMode = false;
		} else {
			defineMode = true;
		}
	}
	// $: changedVersion($schemaVersionStore);
	// function changedVersion(store) {
		
	// }
	

	let defineMode = isStoreEmpty(($schemaVersionsStore));
	let clearFlag = false;
	const newVersion = () => {
		previous = $schemaVersionStore ? $schemaVersionStore.currentVersion : "0.0.0"; //see comment below + would be best to have a "tip-version" from backend or here as a method
		current = $schemaVersionStore ? newCurrent() : "0.0.1";
		function newCurrent() { //rework with semver
			let allCurrentVers = compatibleVersions.map(sv => sv.currentVersion).map(cv => cv.split(".")[2]);
			let highestPatchVer = Math.max(...allCurrentVers);

			let previousVers = $schemaVersionStore.currentVersion.split(".");
			previousVers[2] = Number(highestPatchVer)+1;
			return previousVers.join(".");
		}
		
		defineMode = true;
		clearFlag = !clearFlag;
	}

	const definable = () => (specification && description && $organizationStore && $unitStore && $contextStore && $schemaStore);
	const versionAlreadyExists = (current) => !!compatibleVersions.find(sv => sv.currentVersion === current);

	let oldSpec;
	let newSpec;
	let changes;
	const define = () => {
		if(!definable()) { console.log(errors.SUBMIT); return; }
		if(!validator(previous) || !validator(current)) { console.log(errors.SUBMITVER); return; }
		if(versionAlreadyExists(current)) { console.log(errors.SUBMITVEREXISTS); return; }
		SchemataRepository.createSchemaVersion(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId,
			($schemaStore).schemaId, specification, description, previous, current)
			.then(created => {
				updateStores(created);
				defineMode = false;
			})
			.catch(function (err) {
				err.then(err => {
					console.log(err);
					let result = JSON.parse(err);
					showDiffDialog = true;
                    oldSpec = result.oldSpecification;
                    newSpec = result.newSpecification;
                    changes = result.changes;
				})
                // vm.$store.commit('raiseError', {message: 'Incompatible changes within a compatible version change'}) Alert maybe
            })
		
	}

	function updateStores(obj) {
		console.log({obj});
		$schemaVersionStore = obj;
		$schemaVersionsStore.push(obj);
	}

	let isCreateDisabled = true;
	let isNextDisabled = true;

	$: if(validator(previous) && validator(current) && description && specification && $organizationStore && $unitStore && $contextStore && $schemaStore && defineMode) {
		isCreateDisabled = false;
	} else {
		isCreateDisabled = true;
	}

	$: if(!defineMode) { isNextDisabled = false; }

	let fullyQualified;

	let showDiffDialog = false;
</script>

<svelte:head>
	<title>Schema Version</title>
</svelte:head>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:new={newVersion} on:define={define} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	<Select label="Schema" storeOne={schemaStore} storeAll={schemasStore} arrayOfSelectables={compatibleSchemas} containerClasses="folder-inset3"/>

	<div class="flex-two-col">
		<!-- <ValidatedInput label="Previous Version" bind:value={previous} {clearFlag} validator={validator} invalidString={errors.VERSION} readonly/> -->
		<ValidatedInput label="Current Version (previous was {previous})" placeholder="0.0.0" bind:value={current} {clearFlag} validator={validator} invalidString={errors.VERSION} disabled={!defineMode}/>
		{#if defineMode}
		<ButtonBar center>
			<Button color="primary" text="New Patch" on:click={() => { current = "0.0.2"}}/>
			<Button color="warning" text="New Minor" on:click={() => { current = "0.2.0"}}/>
			<Button color="error" text="New Major" on:click={() => { current = "2.0.0"}}/>
		</ButtonBar>
		{/if}
	</div>
	<ValidatedInput type="textarea" label="Description" bind:value={description} {clearFlag} rows="4" disabled={!defineMode}/>
	<ValidatedInput type="textarea" label="Specification" bind:value={specification} {clearFlag} rows="6" disabled={!defineMode}/>

	<div slot="buttons">
		<ButtonBar>
			<div class="mr-auto">
				<Button color="info" text="New Schema Version" on:click={newVersion}/>
			</div>
			{#if defineMode}
				<Button color="primary" text="Define" on:click={define} disabled={isCreateDisabled}/>
			{/if}
			{#if !isNextDisabled}
				<Button color="primary" outline text={"Home"} href={"."} disabled={isNextDisabled}/>
			{/if}
		</ButtonBar>
	</div>
</CardForm>

<Diff {oldSpec} {newSpec} {changes} bind:showDiffDialog>
	<!-- <Button color="primary" text="Define" on:click={define} disabled={isCreateDisabled}/> -->
</Diff>

{#if description}
	{@html marked(description)}
{/if}

<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}
</style>