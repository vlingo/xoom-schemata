<script>
	import { contextStore, organizationStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitStore } from '../stores';
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull, mdiFileFind, mdiFileUndo, mdiContentSave} from '@mdi/js'
	import { Card, ButtonGroup, ButtonGroupItem, Chip, Radio, Dialog, CardTitle, CardText, Textarea } from 'svelte-materialify/src';
	import SchemataRepository from '../api/SchemataRepository';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import Button from '../components/form/Button.svelte';
	import { marked } from 'marked';
	import DOMPurify from 'dompurify';


	import { createEventDispatcher } from 'svelte';
	const dispatch = createEventDispatcher();

	let specification;
	let description;
	let statusChip;

	let active;
	let showCodeModal = false;
	let chosenLang;
	let langs = Object.entries({
		java: 'Java',
		csharp: 'C#',
	})
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
		if(!showCodeModal) return;
		SchemataRepository.loadSources(($organizationStore).name, ($unitStore).name, ($contextStore).namespace, ($schemaStore).name, ($schemaVersionStore).currentVersion, lang)
			.then(code => {
				console.log({code});
				sourceCode = code;
			})
	};

	const toggleCodeModal = () => showCodeModal = !showCodeModal;
	const togglePreviewModal = () => showPreviewModal = !showPreviewModal;

	$: changedVersionStore($schemaVersionStore);
	$: definable = $schemaVersionStore && $organizationStore && $unitStore && $contextStore && $schemaStore
	$: descDefinable = definable && description
	$: specDefinable = definable && specification
	$: if(chosenLang && typeof chosenLang === "string") sourceCodeFor(chosenLang.toLowerCase());
	$: if(!showCodeModal) {chosenLang = undefined; sourceCode = undefined;}
	$: status = $schemaVersionStore ? $schemaVersionStore.status : "";

	const notEmpty = (value) => !!value ? undefined : errors.EMPTY;

	let selected = 0;
	$: active = !selected ? "spec" : "desc";
</script>


<div class="bottom-container mt-6">
	<div class="bottom-flex">
	<Card class="vl-card pa-6">
		<div style="display: flex">
			<ButtonGroup bind:value={selected} mandatory class="primary-text d-flex">
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
			<Textarea style="margin-top: 1rem" rows="10" outlined bind:value={specification} rules={[notEmpty]} validateOnBlur={!specification}
			disabled={$schemaVersionStore ? $schemaVersionStore.status === "Removed" : true}
			readonly={$schemaVersionStore ? $schemaVersionStore.status !== "Draft" : true}>
				Specification
			</Textarea>
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
			<Textarea style="margin-top: 1rem" rows="10" outlined bind:value={description} rules={[notEmpty]} validateOnBlur={!description}
				disabled={$schemaVersionStore ? $schemaVersionStore.status === "Removed" : true}>
				Description
			</Textarea>
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
				{#each langs as [lang, langName]}
					<Radio bind:group={chosenLang} value={lang} color="primary">{langName}</Radio>
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
				{@html DOMPurify.sanitize(marked(description))}
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
