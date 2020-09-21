<script>
	import StrapButton from 'sveltestrap/src/Button.svelte';
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
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiSourcePull} from '@mdi/js'
	import Icon from '../components/Icon.svelte';
	import ButtonBar from '../components/ButtonBar.svelte';
	import Button from '../components/Button.svelte';

	import { onMount } from 'svelte';

	import { contextsStore, contextStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import SchemataRepository from '../api/SchemataRepository';
	import errors from '../errors';

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

	let activeSpec = true;
	let activeDesc = false;
	let activeOne = true;
	let activeTwo = false;

	let specification = "";
	let description = "";

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
</script>

<Card>
	<CardHeader tag="h3">
		<!-- <FormGroup> -->
			<Label for="search">Search</Label>
			<Input type="search" name="search" id="search" placeholder="search..." />
		<!-- </FormGroup> -->

		<!-- reload button (if needed) -->
	</CardHeader>
	<CardBody>
		<Folder file={root} first={true}/>
	</CardBody>
</Card>


<div class="bottom-container">
	<div class="bottom-left">
		<Card>
			<ListGroup class="py-1">
				<ListGroupItem active={activeOne} tag="button" action on:click={() => {activeOne = true; activeTwo = false;}}>0.0.1</ListGroupItem>
				<ListGroupItem active={activeTwo} tag="button" action on:click={() => {activeTwo = true; activeOne = false;}}>1.0.0</ListGroupItem>
			</ListGroup>
		</Card>
	</div>

	<div class="spacer"></div>

	<div class="bottom-right">
	<Card>
		<ListGroup class="d-flex flex-row p-1">
			<ListGroupItem active={activeSpec} tag="button" action on:click={() => {activeSpec = true; activeDesc = false;}}>Specification</ListGroupItem>
			<ListGroupItem active={activeDesc} tag="button" action on:click={() => {activeDesc = true; activeSpec = false;}}>Description</ListGroupItem>
		</ListGroup>

		{#if activeSpec}
			<ValidatedInput type="textarea" bind:value={specification}/>
			<ButtonBar>
				<Button outline color="primary" icon={mdiLabel} text="PUBLISH" on:click={() => updateStatus("Published")}/>
				<Button outline color="warning" icon={mdiLabelOff} text="DEPRECATE" on:click={() => updateStatus("Deprecated")}/>
				<Button outline color="danger" icon={mdiDelete} text="REMOVE" on:click={() => updateStatus("Removed")}/>
				<Button outline color="info" icon={mdiSourcePull} text="INFO"/>
				<Button color="info" text="SAVE SPECIFICATION" on:click={updateSpecification}/>
			</ButtonBar>
		{:else}
			<ValidatedInput type="textarea" bind:value={description}/>
			<ButtonBar>
				<Button color="success" text="PREVIEW"/>
				<Button color="warning" text="REVERT"/>
				<Button color="info" text="SAVE DESCRIPTION" on:click={updateDescription}/>
			</ButtonBar>
		{/if}
	</Card>
	</div>
</div>

<style>
	.bottom-container {
		margin-top: 1rem;

		display: flex;
		flex-direction: column;
	}
	.spacer {
		width: 10rem;
		display: block;
	}

	.bottom-left {
		width: 10rem;
	}

	@media (min-width: 820px) {
		.bottom-container {
			flex-direction: row;
		}
	}

</style>