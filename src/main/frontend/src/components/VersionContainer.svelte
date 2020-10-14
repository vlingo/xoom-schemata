<script>
	import { contextStore, organizationStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitStore } from '../stores';
	import { isStoreEmpty } from "../utils";
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull, mdiFileFind, mdiFileUndo, mdiContentSave} from '@mdi/js'
	import SchemataRepository from '../api/SchemataRepository';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import ButtonBar from '../components/form/ButtonBar.svelte';
	import Button from '../components/form/Button.svelte';
	import Modal from 'sveltestrap/src/Modal.svelte';
	import ModalHeader from 'sveltestrap/src/ModalHeader.svelte';
	import ModalBody from 'sveltestrap/src/ModalBody.svelte';
	import ModalFooter from 'sveltestrap/src/ModalFooter.svelte';
	import CustomInput from 'sveltestrap/src/CustomInput.svelte';
	import errors from '../errors';
	import marked from 'marked';
	import Card from 'svelte-materialify/src/components/Card';
	import ButtonGroup from 'svelte-materialify/src/components/ButtonGroup';
	import ButtonGroupItem from 'svelte-materialify/src/components/ButtonGroup/ButtonGroupItem.svelte';
	import { createEventDispatcher } from 'svelte';
import Badge from 'svelte-materialify/src/components/Badge';
import Chip from 'svelte-materialify/src/components/Chip';
import {
	  Dialog,
	  CardTitle,
	  CardText,
	  CardActions,
	} from 'svelte-materialify/src';
	const dispatch = createEventDispatcher();

	let specification;
	let description;
	let status;

	let active;

	$: {changedVersionStore($schemaVersionStore)};
	function changedVersionStore($schemaVersionStore) {
		specification = $schemaVersionStore ? $schemaVersionStore.specification : "";
		description = $schemaVersionStore ? $schemaVersionStore.description : "";
		status = $schemaVersionStore ? getStatusString($schemaVersionStore.status) : "";
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

	const updateDescription = () => {
		if(!$schemaVersionStore || !$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.saveSchemaVersionDescription(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, description)
			.then(updated => {
				updateStores(updated, true);
				dispatch("versionChanged", updated);
			})
	}

	const updateSpecification = () => {
		if(!$schemaVersionStore || !$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !specification) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.saveSchemaVersionSpecification(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, specification)
			.then(updated => {
				updateStores(updated, true);
				dispatch("versionChanged", updated);
			})
	}

	const updateStatus = (status) => {
		if(!$schemaVersionStore || !$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !status) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.setSchemaVersionStatus(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, status)
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

	let showCodeModal = false;
	const toggleCodeModal = () => showCodeModal = !showCodeModal;

	// ($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, "java")
	const sourceCodeFor = (lang) => {
		if(lang != "java") return;
		SchemataRepository.loadSources(($organizationStore).name, ($unitStore).name, ($contextStore).namespace, ($schemaStore).name, ($schemaVersionStore).currentVersion, lang)
			.then(code => {
				console.log({code});
				sourceCode = code;
			})
	};
	
	let langs = [
		'Java',
		'C#',
	]
	let sourceCode = "";

	let showPreviewModal = false;
	const togglePreviewModal = () => showPreviewModal = !showPreviewModal;

	const changeActive = (index) => active = index === 0 ? "spec" : "desc"
</script>
  

<div class="bottom-container">
	{#if !isStoreEmpty($schemaVersionStore)}
	<div class="bottom-right">
	<Card>
		<div style="display: flex">
			<ButtonGroup value={[0]} on:change={(e) => changeActive(e.detail[0])} mandatory class="primary-text d-flex">
				<ButtonGroupItem>Specification</ButtonGroupItem>
				<ButtonGroupItem>Description</ButtonGroupItem>
			</ButtonGroup>
			{#if status}
				<!-- <Badge class="ml-4 p-2 align-self-center" color={status.color}>{status.text}</Badge> -->
				<!-- <span style="width: 15rem"></span> -->
				<Chip class="mt-2 mr-2 ml-auto {status.color}-color">{status.text}</Chip>
			{/if}
		</div>
		{#if active=="spec"}
			<ValidatedInput label="Specification" outlined rows="10" type="textarea" bind:value={specification} disabled={$schemaVersionStore.status === "Removed"}/>
			<ButtonBar>
				<Button outlined color="primary" icon={mdiLabel} text="PUBLISH" on:click={() => updateStatus("Published")}/>
				<Button outlined color="warning" icon={mdiLabelOff} text="DEPRECATE" on:click={() => updateStatus("Deprecated")}/>
				<Button outlined color="error" icon={mdiDelete} text="REMOVE" on:click={() => updateStatus("Removed")}/>
				<Button outlined color="info" icon={mdiSourcePull} text="CODE" on:click={toggleCodeModal}/>
				<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateSpecification}/>
			</ButtonBar>
		{:else if active=="desc"}
			<ValidatedInput label="Description" outlined rows="10" type="textarea" bind:value={description} disabled={$schemaVersionStore.status === "Removed"}/>
			<ButtonBar>
				<Button color="success" icon={mdiFileFind} text="PREVIEW" on:click={togglePreviewModal}/>
				<Button color="warning" icon={mdiFileUndo} text="REVERT" on:click={() => description = $schemaVersionStore.description}/>
				<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateDescription}/>
			</ButtonBar>
		{/if}
	</Card>
	</div>
	{/if}
</div>

<Dialog bind:active={showCodeModal}>
	<Card>
		<CardTitle>Choose language to generate:</CardTitle>
		<CardText>
			<div class="mx-3">
				{#each langs as lang}
					<CustomInput type="radio" id={"radio"+lang} name="languageRadio" label={lang} on:change={() => sourceCodeFor(lang.toLowerCase())} />
				{/each}
				{#if sourceCode}
					<pre class="mt-3"><code>{sourceCode}</code></pre>
				{/if}
			</div>
		</CardText>
	</Card>
</Dialog>

<Dialog bind:active={showPreviewModal}>
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
	.bottom-right {
		flex: 1 1 auto;
	}

	@media (min-width: 820px) {
		.bottom-container {
			flex-direction: row;
		}
	}
</style>