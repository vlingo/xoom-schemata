<script>
	import Card from 'sveltestrap/src/Card.svelte';
	import CardBody from 'sveltestrap/src/CardBody.svelte';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import Input from 'sveltestrap/src/Input.svelte';
	import ListGroup from 'sveltestrap/src/ListGroup.svelte';
	import ListGroupItem from 'sveltestrap/src/ListGroupItem.svelte';
	import Folder from '../components/Folder.svelte';
	import marked from 'marked';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull, mdiFileFind, mdiFileUndo, mdiContentSave} from '@mdi/js'
	import ButtonBar from '../components/ButtonBar.svelte';
	import Button from '../components/Button.svelte';

	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import SchemataRepository from '../api/SchemataRepository';
	import errors from '../errors';
	import Modal from 'sveltestrap/src/Modal.svelte';
	import ModalHeader from 'sveltestrap/src/ModalHeader.svelte';
	import ModalBody from 'sveltestrap/src/ModalBody.svelte';
	import ModalFooter from 'sveltestrap/src/ModalFooter.svelte';
	import OrganizationAlert from '../components/alerts/OrganizationAlert.svelte';
	import UnitAlert from '../components/alerts/UnitAlert.svelte';
	import ContextAlert from '../components/alerts/ContextAlert.svelte';
	import SchemaAlert from '../components/alerts/SchemaAlert.svelte';
	import VersionAlert from '../components/alerts/VersionAlert.svelte';
	import { isStoreEmpty } from '../utils';
	import CustomInput from 'sveltestrap/src/CustomInput.svelte';
	import Badge from 'sveltestrap/src/Badge.svelte';

	//could change to organizationId, unitId, etc.
	//also could be reduced to one big function which would reduce for-loops
	// ################
	// for(const org of tree) {
	// 	const compatibleUnits = ($unitsStore).find(u => u.organizationId == org.id);
	// 	if(compatibleUnits) org.files = [];
	// 	for(const unit of compatibleUnits) {
	// 		org.files.push(
	// 			{
	// 				type: 'unit',
	// 				name: unit.name,
	// 				description: unit.description,
	// 				id: unit.unitId
	// 			}
	// 		)
	// 		//you would need something like org.files[0], org.files[1], etc to do this, also still seems non-optimal
	// 	}
	// }
	// ################
	let tree = [];
	tree.files = [];
	// onMount(async () => {
	if(process.browser) {

		for(const org of $organizationsStore) {
			tree.files.push(
				{
					type: 'organization',
					name: org.name,
					description: org.description,
					id: org.organizationId
				}
			)
		}
		
		for(const org of tree.files) {
			const compatibleUnits = ($unitsStore).filter(u => u.organizationId == org.id);
			if(compatibleUnits.length>0) org.files = [];

			for(const unit of compatibleUnits) {
				org.files.push(
					{
						type: 'unit',
						name: unit.name,
						description: unit.description,
						id: unit.unitId
					}
				)
			}
		}
		
		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					const compatibleContexts = ($contextsStore).filter(c => c.unitId == unit.id);
					if(compatibleContexts.length>0) unit.files = [];

					for(const context of compatibleContexts) {
						unit.files.push(
							{
								type: 'context',
								namespace: context.namespace,
								description: context.description,
								id: context.contextId
							}
						)
					}
				}
			}
		}

		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					if(unit.files) {
						for(const context of unit.files) {
							const compatibleSchemas = ($schemasStore).filter(s => s.contextId == context.id);
							if(compatibleSchemas.length>0) context.files = [];

							for(const schema of compatibleSchemas) {
								context.files.push(
									{
										type: 'schema',
										name: schema.name,
										description: schema.description,
										category: schema.category,
										scope: schema.scope,
										id: schema.schemaId
									}
								)
							}
						}
					}
				}
			}
		}

		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					if(unit.files) {
						for(const context of unit.files) {
							if(context.files) {
								for(const schema of context.files) {
									const compatibleSchemaVersions = ($schemaVersionsStore).filter(sv => sv.schemaId == schema.id);
									if(compatibleSchemaVersions.length>0) schema.files = [];
									
									for(const schemaVersion of compatibleSchemaVersions) {
										schema.files.push(
											{
												type: 'schemaVersion',
												status: schemaVersion.status,
												specification: schemaVersion.specification,
												description: schemaVersion.description,
												previous: schemaVersion.previousVersion,
												current: schemaVersion.currentVersion,
												id: schemaVersion.schemaVersionId
											}
										)
									}
								}
							}
						}
					}
				}
			}
		}

		// console.log(document);
	}
	
	let root = [];
	let showcase = {
		files: [
			{
				type: 'organization',
				name: 'VLINGO LCC',
				files: [
					{
						type: 'unit',
						name: 'schemata',
						files: [
							{
								type: 'context',
								name: 'io.vlingo.schemata',
								files: [
									{
										type: 'schema',
										name: 'SchemaDefined',
										// files : [
										// 	{
										// 		type: 'schemaVersion',
										// 		name: '0.0.1'
										// 	},
										// 	{
										// 		type: 'schemaVersion',
										// 		name: '1.0.0'
										// 	}
										// ]
									},
								]
							},
						]
					},
				]
			},
			{
				type: 'organization',
				name: 'Reactive Foundation',
				files: [
					{
						type: 'unit',
						name: 'Alibaba',
					},
					{
						type: 'unit',
						name: 'Lightbend',
					},
					{
						type: 'unit',
						name: 'Netlify',
					},
					{
						type: 'unit',
						name: 'Pivotal',
					},
					{
						type: 'unit',
						name: 'Vlingo',
					},
				]
			},
		]
	};
	// root = showcase;
	root = tree;
	console.log(root);

	let active = "spec";

	let specification;
	let description;

	let status;

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
			case "Removed": return { text: `${status}: Don't use.`, color: "danger"}
		}
	}

	function updateTreeWith(updated) {
		// the tree could also just be a map: root[orgId][unitId]...
		function replaceInTree(rootfiles, updated) {
			let idsFromOrgToVersion = [updated.organizationId, updated.unitId, updated.contextId, updated.schemaId, updated.schemaVersionId];

			function recursiveSearch(array, iteration) {
				const idx = array.findIndex(el => el.id == idsFromOrgToVersion[iteration]);
				//end
				if(iteration >= idsFromOrgToVersion.length-1) {
					console.log(array, idx);
					array[idx] =
					{
						type: 'schemaVersion',
						status: updated.status,
						specification: updated.specification,
						description: updated.description,
						previous: updated.previousVersion,
						current: updated.currentVersion,
						id: updated.schemaVersionId
					};
					return
				}
				recursiveSearch(array[idx].files, ++iteration);
			}
			//start
			recursiveSearch(rootfiles, 0);
		}
		replaceInTree(root.files, updated);
		root = root;
	}
	
	const updateDescription = () => {
		if(!$schemaVersionStore || !$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.saveSchemaVersionDescription(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, description)
			.then(updated => {
				updateStores(updated, true);
				updateTreeWith(updated);
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
				updateTreeWith(updated);
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
				updateTreeWith(updated);
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
</script>


	<Modal isOpen={showCodeModal} toggle={toggleCodeModal} size="lg">
    	<ModalHeader toggle={toggleCodeModal}> <h3> Choose language to generate: </h3></ModalHeader>
    	<ModalBody>
			<div class="mx-3">
				{#each langs as lang}
					<CustomInput type="radio" id={"radio"+lang} name="languageRadio" label={lang} on:change={() => sourceCodeFor(lang.toLowerCase())} />
				{/each}
				<pre class="mt-3"><code>
					{sourceCode}
				</code></pre>
			</div>
    	</ModalBody>
    	<ModalFooter>
      		<!-- <Button color="primary" on:click={toggleCodeModal} text={"Do Something"}/>
      		<Button color="secondary" on:click={toggleCodeModal} text={"Cancel"}/> -->
    	</ModalFooter>
	</Modal>
	
	<Modal isOpen={showPreviewModal} toggle={togglePreviewModal} size="lg">
		<ModalHeader toggle={togglePreviewModal}> <h3> Markup: </h3></ModalHeader>
    	<ModalBody>
			<div class="mx-3">
				{@html marked(description)}
			</div>
    	</ModalBody>
	</Modal>

<Card>
	<CardHeader tag="h3">
		Home
		{#if root.files.length > 0}
			<Button on:click={() => $detailed = !($detailed)} style="float: right" text={"Show Details"}/>
		{/if}
	</CardHeader>

	{#if root.files.length < 1}
		<OrganizationAlert/>
	{:else}
		<!-- <FormGroup> -->
		<Input type="search" name="search" id="search" placeholder="Search..." />
		<!-- </FormGroup> -->
		<!-- reload button (if needed) -->
		<CardBody>
			<Folder detailed={$detailed} file={root} first={true}/>
		</CardBody>
		
		{#if $unitsStore.length < 1}
			<UnitAlert/>
		{:else}
			{#if $contextsStore.length < 1}
				<ContextAlert/>
			{:else}
				{#if $schemasStore.length < 1}
					<SchemaAlert/>
				{:else}
					{#if $schemaVersionsStore.length < 1}
						<VersionAlert/>
					{:else}
						{#if !$organizationStore}
							<OrganizationAlert notChosenAlert/>
						{:else if !$unitStore}
							<UnitAlert notChosenAlert/>
						{:else if !$contextStore}
							<ContextAlert notChosenAlert/>
						{:else if !$schemaStore}
							<SchemaAlert notChosenAlert/>
						{:else if !$schemaVersionStore}
							<VersionAlert notChosenAlert/>
						{/if}
					{/if}
				{/if}
			{/if}
		{/if}
	{/if}
</Card>


<div class="bottom-container">
	{#if !isStoreEmpty($schemaVersionStore)}
	<div class="bottom-right">
	<Card>
		<ListGroup class="d-flex flex-row p-3">
			<ListGroupItem color="primary" class="rounded" active={active==="spec"} tag="button" action on:click={() => active="spec"}>Specification</ListGroupItem>
			<ListGroupItem color="primary" class="rounded" active={active==="desc"} tag="button" action on:click={() => active="desc"}>Description</ListGroupItem>
			{#if status}
				<Badge class="ml-4 p-2 align-self-center" color={status.color}>{status.text}</Badge>
			{/if}
		</ListGroup>
		{#if active=="spec"}
			<ValidatedInput rows="10" type="textarea" bind:value={specification} disabled={$schemaVersionStore.status === "Removed"}/>
			<ButtonBar>
				<Button outline color="primary" icon={mdiLabel} text="PUBLISH" on:click={() => updateStatus("Published")}/>
				<Button outline color="warning" icon={mdiLabelOff} text="DEPRECATE" on:click={() => updateStatus("Deprecated")}/>
				<Button outline color="danger" icon={mdiDelete} text="REMOVE" on:click={() => updateStatus("Removed")}/>
				<Button outline color="info" icon={mdiSourcePull} text="CODE" on:click={toggleCodeModal}/>
				<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateSpecification}/>
			</ButtonBar>
		{:else}
			<ValidatedInput rows="10" type="textarea" bind:value={description} disabled={$schemaVersionStore.status === "Removed"}/>
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


<style>
	.bottom-container {
		margin-top: 1rem;

		display: flex;
		flex-direction: column;
	}
	/* .spacer {
		width: 5rem;
		height: 2rem;
		display: block;
	}

	.bottom-left {
		flex: 0 1 auto;
	} */

	.bottom-right {
		flex: 1 1 auto;
	}

	@media (min-width: 820px) {
		.bottom-container {
			flex-direction: row;
		}
	}

</style>