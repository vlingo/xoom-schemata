<script>
	import { contextStore, organizationStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitStore } from '../stores';
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull, mdiFileFind, mdiFileUndo, mdiContentSave} from '@mdi/js'
	import SchemataRepository from '../api/SchemataRepository';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import Button from '../components/form/Button.svelte';
	import marked from 'marked';
	import Card from 'svelte-materialify/src/components/Card';
	import ButtonGroup from 'svelte-materialify/src/components/ButtonGroup';
	import ButtonGroupItem from 'svelte-materialify/src/components/ButtonGroup/ButtonGroupItem.svelte';
	import { createEventDispatcher, onMount } from 'svelte';
	import Chip from 'svelte-materialify/src/components/Chip';
	import { Dialog, CardTitle, CardText } from 'svelte-materialify/src';
	import Radio from 'svelte-materialify/src/components/Radio';
	const dispatch = createEventDispatcher();

	let specification;
	let description;
	let statusChip;

	let active;
	let showCodeModal = false;
	let chosenLang;
	let langs = [
		'Java',
		'C#',
	]
	let sourceCode = "";
	let showPreviewModal = false;

	function changedVersionStore($schemaVersionStore) {
		specification = $schemaVersionStore ? $schemaVersionStore.specification : "";
		description = $schemaVersionStore ? $schemaVersionStore.description : "";
		statusChip = $schemaVersionStore ? getStatusString($schemaVersionStore.status) : "";
	}
	function getStatusString(status) {
		if(!$schemaVersionStore) return;
		switch(status) {
			case "Draft": return { text: `${status}: May change.`, color: "warning"}
			case "Published": return { text: `${status}: Production ready.`, color: "success"}
			case "Deprecated": return { text: `${status}: Consumers should replace.`, color: "warning"}
			case "Removed": return { text: `${status}: Don't use.`, color: "error"}
		}
	}

	const statusRedefinable = (status) => definable && status;
	const updateDescription = () => {
		if(!descDefinable) return;
		SchemataRepository.patchSchemaVersionDescription(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, description)
			.then(updated => {
				updateStores(updated, true);
				dispatch("versionChanged", updated);
			})
	}

	const updateSpecification = () => {
		if(!specDefinable) return;
		SchemataRepository.patchSchemaVersionSpecification(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, specification)
			.then(updated => {
				updateStores(updated, true);
				dispatch("versionChanged", updated);
			})
	}

	const updateStatus = (updatedStatus) => {
		if(!statusRedefinable(updatedStatus)) return;
		SchemataRepository.patchSchemaVersionStatus(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, updatedStatus)
			.then(updated => {
				updateStores(updated, true);
				dispatch("versionChanged", updated);
			})
	}
	function updateStores(obj, reset = false) {
		console.log({obj});
		$schemaVersionStore = obj;
		if(reset) $schemaVersionsStore = ($schemaVersionsStore).filter(schemaVersion => schemaVersion.schemaVersionId != ($schemaVersionStore).schemaVersionId);
		$schemaVersionsStore.push(obj);
	}

	// ($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, "java")
	const sourceCodeFor = (lang) => {
		if(lang != "java" || !showCodeModal) return;
		SchemataRepository.loadSources(($organizationStore).name, ($unitStore).name, ($contextStore).namespace, ($schemaStore).name, ($schemaVersionStore).currentVersion, lang)
			.then(code => {
				console.log({code});
				sourceCode = code;
			})
	};

	const toggleCodeModal = () => showCodeModal = !showCodeModal;
	const togglePreviewModal = () => showPreviewModal = !showPreviewModal;
	const changeActive = (index) => active = index === 0 ? "spec" : "desc";

	$: changedVersionStore($schemaVersionStore);
	$: definable = $schemaVersionStore && $organizationStore && $unitStore && $contextStore && $schemaStore
	$: descDefinable = definable && description
	$: specDefinable = definable && specification
	$: if(chosenLang && typeof chosenLang === "string") sourceCodeFor(chosenLang.toLowerCase());
	$: if(!showCodeModal) {chosenLang = undefined; sourceCode = undefined;}
	$: status = $schemaVersionStore ? $schemaVersionStore.status : "";
</script>


<div class="bottom-container">
	<div class="bottom-flex">
	<Card>
		<div style="display: flex">
			<ButtonGroup value={[0]} on:change={(e) => changeActive(e.detail[0])} mandatory class="primary-text d-flex">
				<ButtonGroupItem>Specification</ButtonGroupItem>
				<ButtonGroupItem>Description</ButtonGroupItem>
			</ButtonGroup>
			{#if statusChip}
				<!-- <span style="width: 15rem"></span> -->
				<Chip class="mt-2 mr-2 ml-auto {statusChip.color}-color">{statusChip.text}</Chip>
			{/if}
		</div>
		{#if active=="spec"}
			<!-- <wc-monaco-editor style="width: 800px; height: 800px; display: block;" language="javascript"></wc-monaco-editor> -->
			<ValidatedInput label="Specification" outlined rows="10" type="textarea" bind:value={specification} disabled={$schemaVersionStore ? $schemaVersionStore.status === "Removed" : true} readonly={$schemaVersionStore ? $schemaVersionStore.status !== "Draft" : true}/>
			<ButtonBar>
				{#if status !== "Removed"}
					{#if status === "Draft"}
						<Button outlined color="primary" icon={mdiLabel} text="PUBLISH" on:click={() => updateStatus("Published")}/>
					{/if}
					{#if status === "Published" || status === "Draft"}
						<Button outlined color="warning" icon={mdiLabelOff} text="DEPRECATE" on:click={() => updateStatus("Deprecated")}/>
					{/if}
					<Button outlined color="error" icon={mdiDelete} text="REMOVE" on:click={() => updateStatus("Removed")}/>
				{/if}
				<Button outlined color="info" icon={mdiSourcePull} text="CODE" on:click={toggleCodeModal}/>
				{#if status === "Draft"}
					<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateSpecification}  disabled={!specDefinable}/>
				{/if}
			</ButtonBar>
		{:else if active=="desc"}
			<ValidatedInput label="Description" outlined rows="10" type="textarea" bind:value={description} disabled={$schemaVersionStore ? $schemaVersionStore.status === "Removed" : true}/>
			<ButtonBar>
				<Button color="success" icon={mdiFileFind} text="PREVIEW" on:click={togglePreviewModal}/>
				<Button color="warning" icon={mdiFileUndo} text="REVERT" on:click={() => description = $schemaVersionStore.description}/>
				<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateDescription} disabled={!descDefinable}/>
			</ButtonBar>
		{/if}
	</Card>
	</div>
</div>

<Dialog width={800} bind:active={showCodeModal}>
	<Card>
		<CardTitle>Choose language to generate:</CardTitle>
		<CardText>
			<div class="mx-3">
				{#each langs as lang}
					<Radio bind:group={chosenLang} value={lang} color="primary">{lang}</Radio>
				{/each}
				{#if sourceCode}
					<pre class="mt-3"><code>{sourceCode}</code></pre>
				{/if}
			</div>
		</CardText>
	</Card>
</Dialog>

<Dialog width={1000} bind:active={showPreviewModal}>
	<Card>
		<CardTitle>Markup:</CardTitle>
		<CardText>
			<div class="mx-3">
				{@html marked(description)}
			</div>
		</CardText>
	</Card>
</Dialog>

<style>
	.bottom-container {
		margin-top: 1rem;

		display: flex;
		flex-direction: column;
	}
	.bottom-flex {
		flex: 1 1 auto;
	}

	@media (min-width: 820px) {
		.bottom-container {
			flex-direction: row;
		}
	}
	pre {
		background-color: #f0f0f0;
		border-radius: 4px;
	}
	code {
		font-family: menlo, inconsolata, monospace;
		font-size: calc(1em - 2px);
		color: #555;
		background-color: #f0f0f0;
		padding: 0.2em 0.4em;
		border-radius: 2px;
	}
</style>