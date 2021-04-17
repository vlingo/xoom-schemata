<script>
	import { TextField, Textarea } from 'svelte-materialify/src';
	import OrganizationSelect from '../components/form/OrganizationSelect.svelte';
	import UnitSelect from '../components/form/UnitSelect.svelte';
	import ContextSelect from '../components/form/ContextSelect.svelte';
	import SchemaSelect from '../components/form/SchemaSelect.svelte';
	import VersionSelect from '../components/form/VersionSelect.svelte';
	import CardForm from '../components/form/CardForm.svelte';
	import Button from '../components/form/Button.svelte';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import VersionButtons from '../components/form/VersionButtons.svelte';
	import MarkdownPreview from '../components/form/MarkdownPreview.svelte';
	import Diff from '../components/Diff.svelte';
	import SchemataRepository from '../api/SchemataRepository';
	import { contextsStore, contextStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { isEmpty } from '../utils';
	import errors from '../errors';
	import { mdiChevronLeft } from '@mdi/js';
	import { writable } from 'svelte/store';
	const versionRule = (v) => {
		return /^\d+\.\d+\.\d+$/.test(v) ? undefined : errors.VERSION
	}
	const notEmptyRule = (value) => !!value ? undefined : errors.EMPTY;

	let current;
	let previous;
	let description;
	let specification;

	let compatibleUnits = writable([]);
	let compatibleContexts = writable([]);
	let compatibleSchemas = writable([]);
	let compatibleVersions = writable([]);

	let showDiffDialog = false;
	let fullyQualified;

	function changedOrganization(store) {
		$compatibleUnits = store ? $unitsStore.filter(u => u.organizationId == store.organizationId) : [];
		$unitStore = $compatibleUnits.length > 0 ? $compatibleUnits[$compatibleUnits.length-1] : undefined;
	}

	function changedUnit(store) {
		$compatibleContexts = store ? $contextsStore.filter(c => c.unitId == store.unitId) : [];
		$contextStore = $compatibleContexts.length > 0 ? $compatibleContexts[$compatibleContexts.length-1] : undefined;
	}

	function changedContext(store) {
		$compatibleSchemas = store ? $schemasStore.filter(s => s.contextId == store.contextId) : [];
		$schemaStore = $compatibleSchemas.length > 0 ? $compatibleSchemas[$compatibleSchemas.length-1] : undefined;
	}

	function changedSchema(store) {
		$compatibleVersions = store ? $schemaVersionsStore.filter(v => v.schemaId == store.schemaId) : [];
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

	const versionAlreadyExists = (current) => !!$compatibleVersions.find(sv => sv.currentVersion === current);

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
		$schemaVersionsStore = [...$schemaVersionsStore, obj]
	}
	function updateSelects() {
		$compatibleVersions = $schemaStore ? $schemaVersionsStore.filter(v => v.schemaId == $schemaStore.schemaId) : [];
	}

	$: changedOrganization($organizationStore)
	$: changedUnit($unitStore)
	$: changedContext($contextStore);
	$: changedSchema($schemaStore);
	$: changedVersion($schemaVersionStore);
	$: definable = specification && description && $organizationStore && $unitStore && $contextStore && $schemaStore && !versionRule(previous) && !versionRule(current) && !versionAlreadyExists(current);
	$: showVersionSelect = !isEmpty(($schemaVersionsStore));
</script>

<svelte:head>
	<title>Schema Version</title>
</svelte:head>

<CardForm title="Schema Version" linkToNext="Home" href="/" on:new={newVersion} on:define={define} {defineMode} {fullyQualified}>
	<OrganizationSelect/>
	<UnitSelect {compatibleUnits}/>
	<ContextSelect {compatibleContexts}/>
	<SchemaSelect {compatibleSchemas}/>
	{#if showVersionSelect}
		<VersionSelect {compatibleVersions}/>
	{/if}
	<div class="flex-two-col">
		<TextField class="mb-4 pb-4" placeholder="0.0.0" bind:value={current} rules={[notEmptyRule, versionRule]} disabled={!defineMode}>
			Current Version (previous was {previous})
		</TextField>
		{#if defineMode}
			<VersionButtons bind:currentVersion={current}/>
		{/if}
	</div>
	<Textarea outlined placeholder="Markdown Description" bind:value={description} rules={[notEmptyRule]} disabled={!defineMode}>
		Description
	</Textarea>
	<MarkdownPreview {description}/>
	<Textarea rows="8" outlined placeholder="Specify your schema" bind:value={specification} rules={[notEmptyRule]} disabled={!defineMode}>
		Specification
	</Textarea>

	<div style="flex:1;" slot="buttons">
		<ButtonBar>
			<Button outlined color="primary" text="Prev" icon={mdiChevronLeft} href="schema"/>
			<div class="mr-auto">
				<Button color="info" text="New Schema Version" on:click={newVersion}/>
			</div>
			{#if defineMode}
				<Button color="primary" text="Define" on:click={define} disabled={!definable}/>
			{:else}
				<Button color="primary" outline text={"Home"} href={"."} />
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
