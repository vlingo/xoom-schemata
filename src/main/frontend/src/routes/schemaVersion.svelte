<script>
	import CardForm from '../components/form/CardForm.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import Button from '../components/form/Button.svelte';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import Select from '../components/form/Select.svelte';
	import marked from 'marked';
	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { isEmpty } from '../utils';
	import errors from '../errors';
	import Diff from '../components/Diff.svelte';
	import Card from 'svelte-materialify/src/components/Card';
	const validator = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v) ? undefined : errors.VERSION
	}
	const versionPattern = /(\d+)\.(\d+)\.(\d+)/;
	function clickedVersionButton(type) {
		if($schemaStore && $schemaVersionsStore.find(v => v.schemaId === $schemaStore.schemaId)) {
			let versionsWithSameSchemaAsCurrent = $schemaVersionsStore.filter(v => v.schemaId === $schemaStore.schemaId);
			console.log(versionsWithSameSchemaAsCurrent);
			let highestVersion = versionsWithSameSchemaAsCurrent.map(v => v.currentVersion).sort(sortVersions).pop();
			console.log(highestVersion);
			let [, major, minor, patch] = versionPattern.exec(highestVersion);
			switch(type) {
				case "patch": ++patch; break;
				case "minor": ++minor; patch = 0; break;
				case "major": ++major; minor = 0; patch = 0; break;
			}
			current = `${major}.${minor}.${patch}`;
			console.log(current);
		}
	}
	//TODO: review edge-cases
	function sortVersions(a, b) {
		let [, majorA, minorA, patchA] = versionPattern.exec(a);
		let [, majorB, minorB, patchB] = versionPattern.exec(b);
		if(majorA > majorB) {
			return 1;
		}
		if(majorA == majorB && minorA > minorB) {
			return 1;
		}
		if(majorA == majorB && minorA == minorB && patchA > patchB) {
			return 1;
		}
		return 0;
	}

	let current;
	let previous;
	let description;
	let specification;

	let compatibleUnits = [];
	let compatibleContexts = [];
	let compatibleSchemas = [];
	let compatibleVersions = [];

	let showDiffDialog = false;
	let fullyQualified;
	
	function changedOrganization(store) {
		compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = compatibleUnits.length > 0 ? compatibleUnits[compatibleUnits.length-1] : undefined;
	}
	
	function changedUnit(store) {
		compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = compatibleContexts.length > 0 ? compatibleContexts[compatibleContexts.length-1] : undefined;
	}
	
	function changedContext(store) {
		compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = compatibleSchemas.length > 0 ? compatibleSchemas[compatibleSchemas.length-1] : undefined;
	}
	
	function changedSchema(store) {
		compatibleVersions = store ? $schemaVersionsStore.filter(v => v.schemaId == store.schemaId) : [];
		$schemaVersionStore = store ? $schemaVersionsStore.find(v => v.schemaId == store.schemaId) : undefined;

		store && !$schemaVersionStore ? specification = `${store.category.toLowerCase()} ${store.name} {\n\t\n\t\n\t\n\t\n\t\n}` : "";
	}
	
	function changedVersion(store) {
		if(store) {
			// current = store.currentVersion;
			current = "";
			previous = store.currentVersion;
			description = store.description;
			specification = store.specification;
		} else {
			current = "0.0.1";
			previous = "0.0.0"
			description = "";
			specification = "";
		}
	}
	

	let defineMode = isEmpty(($schemaVersionsStore));
	const newVersion = () => {
		defineMode = true;
	}
	
	const versionAlreadyExists = (current) => !!compatibleVersions.find(sv => sv.currentVersion === current);

	let oldSpec;
	let newSpec;
	let changes;
	//FIXME: this shouldn't be able to jump from 0.0.0 to 1.0.0 and then again from 0.0.0 to 1.0.1. It needs to take the closest available version under it as comparison, not 0.0.0.
	const define = () => {
		if(!definable) return;
		SchemataRepository.createSchemaVersion(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId,
			($schemaStore).schemaId, specification, description, previous, current)
			.then(created => {
				updateStores(created);
				updateSelects();
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
                //'Incompatible changes within a compatible version change' Alert maybe
            })
		
	}

	function updateStores(obj) {
		console.log({obj});
		$schemaVersionStore = obj;
		$schemaVersionsStore.push(obj);
	}
	function updateSelects() {
		// maybe also other compatibles..
		compatibleVersions = $schemaStore ? $schemaVersionsStore.filter(v => v.schemaId == $schemaStore.schemaId) : [];
	}

	$: changedOrganization($organizationStore)
	$: changedUnit($unitStore)
	$: changedContext($contextStore);
	$: changedSchema($schemaStore);
	$: changedVersion($schemaVersionStore);
	$: definable = specification && description && $organizationStore && $unitStore && $contextStore && $schemaStore && !validator(previous) && !validator(current) && !versionAlreadyExists(current);
	$: showVersionSelect = !isEmpty(($schemaVersionsStore));
	$: {
		console.log($schemaVersionsStore);
	}
</script>

<svelte:head>
	<title>Schema Version</title>
</svelte:head>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:new={newVersion} on:define={define} {defineMode} {fullyQualified}>
	<Select label="Organization" storeOne={organizationStore} storeAll={organizationsStore} arrayOfSelectables={$organizationsStore}/>
	<Select label="Unit" storeOne={unitStore} storeAll={unitsStore} arrayOfSelectables={compatibleUnits} containerClasses="folder-inset1"/>
	<Select label="Context" storeOne={contextStore} storeAll={contextsStore} arrayOfSelectables={compatibleContexts} containerClasses="folder-inset2"/>
	<Select label="Schema" storeOne={schemaStore} storeAll={schemasStore} arrayOfSelectables={compatibleSchemas} containerClasses="folder-inset3"/>
	{#if showVersionSelect}
		<Select label="Schema Version" storeOne={schemaVersionStore} storeAll={schemaVersionsStore} arrayOfSelectables={compatibleVersions} containerClasses="folder-inset4"/>
	{/if}
	<div class="flex-two-col">
		<!-- <ValidatedInput label="Previous Version" bind:value={previous} validator={validator} readonly/> -->
		<ValidatedInput label="Current Version (previous was {previous})" placeholder="0.0.0" bind:value={current} validator={validator} disabled={!defineMode}/>
		{#if defineMode}
		<ButtonBar center>
			<Button color="error" text="New Major" on:click={() => clickedVersionButton("major")}/>
			<Button color="warning" text="New Minor" on:click={() => clickedVersionButton("minor")}/>
			<Button color="primary" text="New Patch" on:click={() => clickedVersionButton("patch")}/>
		</ButtonBar>
		{/if}
	</div>
	<ValidatedInput outlined type="textarea" label="Description" bind:value={description} disabled={!defineMode}/>

	<Card disabled={!description} class="ma-2 pl-5 pt-2 pb-5 pr-2" style="min-height: 5rem">
		<div id="markdown-container">
			{#if description}
				{@html marked(description)}
			{:else}
				{@html marked("##### &#35;&#35;&#35;&#35; Write some &#95;_markdown_&#95; into &#42;&#42;**Description**&#42;&#42;")}
			{/if}
		</div>
	</Card>
	<ValidatedInput rows="8" outlined type="textarea" label="Specification" bind:value={specification} disabled={!defineMode}/>

	<div style="flex:1;" slot="buttons">
		<ButtonBar>
			<div class="mr-auto">
				<Button color="info" text="New Schema Version" on:click={newVersion}/>
			</div>
			{#if defineMode}
				<Button color="primary" text="Define" on:click={define} disabled={!definable}/>
			{/if}
			{#if !defineMode}
				<Button color="primary" outline text={"Home"} href={"."} disabled={!defineMode}/>
			{/if}
		</ButtonBar>
	</div>
</CardForm>

<Diff {oldSpec} {newSpec} {changes} bind:showDiffDialog>
	<!-- <Button color="primary" text="Define" on:click={define} disabled={isCreateDisabled}/> -->
</Diff>

<style>
	.flex-two-col {
		display: flex;
		flex-wrap: wrap;
	}

	:global(#markdown-container h1) {
		font-size: 4rem;
	}
</style>