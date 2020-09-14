

<!-- <script context="module">
	export async function preload(page, session) {
		const { slug } = page.params;

		const res = await this.fetch(`blog/${slug}.json`);
		const article = await res.json();

		return { article };
	}
</script> -->




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

	import { contextsStore, organizationsStore, schemasStore, schemaVersionsStore, unitsStore } from '../stores';
	import { onMount } from 'svelte';

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
									if(compatibleSchemaVersions.length>0) context.files = [];
									
									for(const schemaVersion of compatibleSchemaVersions) {
										context.files.push(
											{
												type: 'schemaVersion',
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

		// console.log(document.querySelector(".navbar-brand"));
		// console.log("index");

		// const orgs = await SchemataRepository.getOrganizations();
		// console.log({orgs});
		// const newOrg = await SchemataRepository.createOrganization("test2", "test2desc");
		// console.log({newOrg});
		// const orgs2 = await SchemataRepository.getOrganizations();
		// console.log({orgs2});

	// 	const module = await import('my-non-ssr-component');
	// 	MyComponent = module.default;
	}
	// if (process.browser) {}

	// fetch(url, {
	// 	method: 'POST',
	// 	body: JSON.stringify(data),
	// 	headers: {
	// 		'Content-Type': 'application/json'
	// 	}
	// })
	// .then(r => {
	// 	r.json()
	// 	.then(function(result) {
	// 		// The data is posted: do something with the result...
	// 		console.log(result);
	// 	})
	// })
	// .catch(err => { })
	let root;
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
				<Button outline color="primary" icon={mdiLabel} text="PUBLISH"/>
				<Button outline color="warning" icon={mdiLabelOff} text="DEPRECATE"/>
				<Button outline color="danger" icon={mdiDelete} text="REMOVE"/>
				<Button outline color="info" icon={mdiSourcePull} text="INFO"/>
				<Button color="info" text="SAVE SPECIFICATION"/>
			</ButtonBar>
		{:else}
			<ValidatedInput type="textarea" bind:value={description}/>
			<ButtonBar>
				<Button color="success" text="PREVIEW"/>
				<Button color="warning" text="REVERT"/>
				<Button color="info" text="SAVE DESCRIPTION"/>
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