<script>
	import CardForm from '../components/CardForm.svelte';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import Button from '../components/Button.svelte';
	import ButtonBar from '../components/ButtonBar.svelte';
	import Select from '../components/Select.svelte';

	import marked from 'marked';

	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { isStoreEmpty } from '../utils';
	import errors from '../errors';
	const validator = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v)
	}

	let description = $schemaVersionStore? $schemaVersionStore.description : "";
	let previous = $schemaVersionStore? $schemaVersionStore.previousVersion : "0.0.0";
	let current = $schemaVersionStore? $schemaVersionStore.currentVersion : "0.0.1";
	let specification = $schemaVersionStore? $schemaVersionStore.specification : "";

	$: compatibleUnits = changedUnits($organizationStore)
	function changedUnits(store) {
		store ? $unitStore = $unitsStore.find(u => u.organizationId == store.organizationId) : $unitStore = undefined;
		return store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
	}
	$: compatibleContexts = changedContexts($unitStore)
	function changedContexts(store) {
		store ? $contextStore = $contextsStore.find(c => c.unitId == store.unitId) : $contextStore = undefined;
		return store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
	}
	$: compatibleSchemas = changedSchemas($contextStore);
	function changedSchemas(store) {
		store ? $schemaStore = $schemasStore.find(s => s.contextId == store.contextId) : $schemaStore = undefined;
		return store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
	}
	$: compatibleVersions = changedVersions($schemaStore);
	function changedVersions(store) {
		store ? $schemaVersionStore = $schemaVersionsStore.find(v => v.schemaId == store.schemaId) : $schemaVersionStore = undefined;
		store ? specification = `${store.category.toLowerCase()} ${store.name} {\n\t\n}` : "";
		if($schemaVersionStore) {
			description = $schemaVersionStore.description;
			specification = $schemaVersionStore.specification;
		}
		return store ? $schemaVersionsStore.filter(v => v.schemaId == store.schemaId) : [];
	}
	

	let defineMode = isStoreEmpty(($schemaVersionsStore));
	let clearFlag = false;
	const newVersion = () => {
		previous = $schemaVersionStore ? $schemaVersionStore.currentVersion : "0.0.0"; //see comment below + would be best to have a "tip-version" from backend or here as a method
		current = $schemaVersionStore ? newCurrent() : "0.0.1";
		function newCurrent() { //we could also say that only the latest version currently defined shoud be shown on this page, then we just increment previousVers[2].
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

	$: if($schemaVersionStore) { isNextDisabled = false; }

	let fullyQualified;
</script>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:new={newVersion} on:define={define} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	<Select label="Schema" storeOne={schemaStore} storeAll={schemasStore} arrayOfSelectables={compatibleSchemas} containerClasses="folder-inset3"/>

	<div class="flex-two-col">
		<ValidatedInput label="Previous Version" bind:value={previous} {clearFlag} validator={validator} invalidString={errors.VERSION} disabled={!defineMode}/>
		<ValidatedInput label="Current Version" bind:value={current} {clearFlag} validator={validator} invalidString={errors.VERSION} disabled={!defineMode}/>
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