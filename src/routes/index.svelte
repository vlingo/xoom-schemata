<script>
	import Card from 'sveltestrap/src/Card.svelte';
	import CardBody from 'sveltestrap/src/CardBody.svelte';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import Input from 'sveltestrap/src/Input.svelte';
	import Label from 'sveltestrap/src/Label.svelte';
	import ListGroup from 'sveltestrap/src/ListGroup.svelte';
	import ListGroupItem from 'sveltestrap/src/ListGroupItem.svelte';
	import Folder from '../components/Folder.svelte';
	import marked from 'marked';
	import ValidatedInput from '../components/ValidatedInput.svelte';
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull, mdiFileFind, mdiFileUndo, mdiContentSave} from '@mdi/js'
	import ButtonBar from '../components/ButtonBar.svelte';
	import Button from '../components/Button.svelte';

	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import SchemataRepository from '../api/SchemataRepository';
	import errors from '../errors';
	import ClickableListItem from '../components/ClickableListItem.svelte';
	import Modal from 'sveltestrap/src/Modal.svelte';
import ModalHeader from 'sveltestrap/src/ModalHeader.svelte';
import ModalBody from 'sveltestrap/src/ModalBody.svelte';
import ModalFooter from 'sveltestrap/src/ModalFooter.svelte';
import Alert from 'sveltestrap/src/Alert.svelte';

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
												previous: schemaVersion.previous,
												current: schemaVersion.current,
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

	let specification = "";
	let description = "";

	// let schemaVersions = $schemaVersionsStore; 
	$: schemaVersions = $schemaVersionsStore.filter(ver => ver.schemaId == $schemaStore.schemaId);

	function updateTreeWith(updated) {
		// I know, this is bad, could at least be recursive or the tree could be a map: tree[orgId][unitId]...
		// or reload the page, but that takes longer
		// or abuse Folder.svelte to change leaf element if it is "type=schemaVersion" and "{#if} string:newDescription" or something like that
		const oidx = root.files.findIndex(org => org.id == updated.organizationId);
		const uidx = root.files[oidx].files.findIndex(unit => unit.id == updated.unitId);
		const cidx = root.files[oidx].files[uidx].files.findIndex(context => context.id == updated.contextId)
		const sidx = root.files[oidx].files[uidx].files[cidx].files.findIndex(schema => schema.id == updated.schemaId)
		const svidx = root.files[oidx].files[uidx].files[cidx].files[sidx].files.findIndex(schemaVersion => schemaVersion.id == updated.schemaVersionId)
		root.files[oidx].files[uidx].files[cidx].files[sidx].files[svidx] = 
		{
			type: 'schemaVersion',
			status: updated.status,
			specification: updated.specification,
			description: updated.description,
			previous: updated.previous,
			current: updated.current,
			id: updated.schemaVersionId
		};
	}
	// stores suboptimal, you would need to be able to select them (e.g. in Folder structure)
	const updateDescription = () => {
		if(!$schemaVersionStore || !$organizationStore || !$unitStore || !$contextStore || !$schemaStore || !description) {
			console.log(errors.SUBMIT);
			return;
		}
		SchemataRepository.saveSchemaVersionDescription(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, description)
			.then(updated => {
				console.log({updated});
				$schemaVersionStore = updated;
				$schemaVersionsStore = ($schemaVersionsStore).filter(schemaVersion => schemaVersion.schemaVersionId != ($schemaVersionStore).schemaVersionId);
				$schemaVersionsStore.push(updated);
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
				console.log({updated});
				$schemaVersionStore = updated;
				$schemaVersionsStore = ($schemaVersionsStore).filter(schemaVersion => schemaVersion.schemaVersionId != ($schemaVersionStore).schemaVersionId);
				$schemaVersionsStore.push(updated);
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
				console.log({updated});
				$schemaVersionStore = updated;
				$schemaVersionsStore = ($schemaVersionsStore).filter(schemaVersion => schemaVersion.schemaVersionId != ($schemaVersionStore).schemaVersionId);
				$schemaVersionsStore.push(updated);
				updateTreeWith(updated);
			})
	}
	let showModal = false;
	const toggleSourceModal = () => {
		showModal = !showModal;
		if(showModal) {
			SchemataRepository.loadSources(($organizationStore).name, ($unitStore).name, ($contextStore).namespace, ($schemaStore).name, ($schemaVersionStore).currentVersion, "java")
				// ($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, "java")
				.then(code => {
					console.log({code});
				})
		}
	} 
</script>


	<Modal isOpen={showModal} toggle={toggleSourceModal}>
    	<ModalHeader toggle={toggleSourceModal}>Modal title</ModalHeader>
    	<ModalBody>
			<!-- {#await SchemataRepository.loadSources(($organizationStore).organizationId, ($unitStore).unitId, ($contextStore).contextId, ($schemaStore).schemaId, ($schemaVersionStore).schemaVersionId, "java") then test}
				{test}
			{/await} -->
    	</ModalBody>
    	<ModalFooter>
      		<Button color="primary" on:click={toggleSourceModal} text={"Do Something"}/>
      		<Button color="secondary" on:click={toggleSourceModal} text={"Cancel"}/>
    	</ModalFooter>
  	</Modal>
	<Modal>
		<!-- marked -->
	</Modal>

<Card>
	<CardHeader tag="h3">
		Home
	</CardHeader>

	{#if root.files.length > 0}
		<!-- <FormGroup> -->
			<Input type="search" name="search" id="search" placeholder="Search..." />
		<!-- </FormGroup> -->
		<!-- reload button (if needed) -->
	<CardBody>
		<Folder file={root} first={true}/>
	</CardBody>
	{:else}
		<Alert color="info">
		<h4 class="alert-heading text-capitalize">Tree-View Placeholder</h4>
    	<p>When you have defined something, it will be shown inside a Tree-View right here.</p>
		<p>
			Start by defining an <a href="/organization" class="alert-link">Organization</a>.
		</p>
		</Alert>
		
	{/if}
</Card>

{#if schemaVersions.length > 0}
<div class="bottom-container">
		<div class="bottom-left">
			<Card>
				<ListGroup class="py-1">
					{#each schemaVersions as schemaVersion}
						<ClickableListItem version={schemaVersion}>
							{schemaVersion.currentVersion}
						</ClickableListItem>
					{/each}
				</ListGroup>
			</Card>
		</div>

	<div class="spacer"></div>

	<div class="bottom-right">
		<Card>
			<ListGroup class="d-flex flex-row p-1">
				<ListGroupItem active={active=="spec"} tag="button" action on:click={() => active = "spec"}>Specification</ListGroupItem>
				<ListGroupItem active={active=="desc"} tag="button" action on:click={() => active = "desc"}>Description</ListGroupItem>
			</ListGroup>

			{#if active=="spec"}
				<ValidatedInput type="textarea" bind:value={specification}/>
				<ButtonBar>
					<Button outline color="primary" icon={mdiLabel} text="PUBLISH" on:click={() => updateStatus("Published")}/>
					<Button outline color="warning" icon={mdiLabelOff} text="DEPRECATE" on:click={() => updateStatus("Deprecated")}/>
					<Button outline color="danger" icon={mdiDelete} text="REMOVE" on:click={() => updateStatus("Removed")}/>
					<Button outline color="info" icon={mdiSourcePull} text="CODE" on:click={toggleSourceModal}/>
					<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateSpecification}/>
				</ButtonBar>
			{:else}
				<ValidatedInput type="textarea" bind:value={description}/>
				<ButtonBar>
					<Button color="success" icon={mdiFileFind} text="PREVIEW"/>
					<Button color="warning" icon={mdiFileUndo} text="REVERT"/>
					<Button color="info" icon={mdiContentSave} text="SAVE" on:click={updateDescription}/>
				</ButtonBar>
			{/if}
		</Card>
	</div>
</div>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Schema Version Edit Placeholder</h4>
    	<p>When you have defined Schema Versions, you can edit them right here.</p>
		<p>
			Define a <a href="/schemaVersion" class="alert-link">Schema Version</a>,
			or, if you have already, choose one of the Schemas in the Tree-View.
		</p>
	</Alert>
{/if}

<style>
	.bottom-container {
		margin-top: 1rem;

		display: flex;
		flex-direction: column;
	}
	.spacer {
		width: 5rem;
		height: 2rem;
		display: block;
	}

	.bottom-left {
		flex: 0 1 auto;
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