
<!-- <script context="module">
	export async function preload(page, session) {
		// `this.fetch` is a wrapper around `fetch` that allows you to make credentialled requests on both server and client
		var _this = this;

		let tree = [];

		async function fetchOrgsInto(tree) {
			const res = await _this.fetch('http://localhost:9019/organizations');
			const orgs = await res.json();
			for(const org of orgs) {
				tree.push(
					{
						type: 'organization',
						name: org.name,
						id: org.organizationId,
					}
				)
			}
		};
		await fetchOrgsInto(tree);
		console.log("after orgs:", {tree});

		async function fetchUnitsInto(tree) {
			for(const org of tree) {
				const res = await _this.fetch(`http://localhost:9019/organizations/${org.id}/units`);
				const units = await res.json();
				for(const unit of units) {
					if(!org.files) org.files = [];
					org.files.push(
						{
							type: 'unit',
							name: unit.name,
							id: unit.unitId,
						}
					)
				}
			}
		};
		await fetchUnitsInto(tree);
		// console.log("after units:", {tree}, tree[0].files);

		async function fetchContextsInto(tree) {
			for(const org of tree) {
				if(org.files) {
					for(const unit of org.files) {
						const res = await _this.fetch(`http://localhost:9019/organizations/${org.id}/units/${unit.id}/contexts`);
						const contexts = await res.json();

						for(const context of contexts) {
							if(!unit.files) unit.files = [];
							unit.files.push(
								{
									type: 'context',
									name: context.namespace,
									id: context.contextId,
								}
							)
						}
					}
				}
			}
		};
		await fetchContextsInto(tree);
		// console.log("after contexts:", {tree}, tree[0].files, tree[0].files[0].files);


		return { tree };
	}
</script> -->

<!-- <script context="module">
	export async function preload(page, session) {
		const { slug } = page.params;

		const res = await this.fetch(`blog/${slug}.json`);
		const article = await res.json();

		return { article };
	}
</script> -->




<script>
	import SchemataRepository from '../api/SchemataRepository';
	import { onMount } from 'svelte';
	// let MyComponent;
	onMount(async () => {
		// console.log(document.querySelector(".navbar-brand"));
		console.log("index");

		const orgs = await SchemataRepository.getOrganizations();
		console.log({orgs});
		// const test = await SchemataRepository.createOrganization("test2", "test2desc")
		

	// 	const module = await import('my-non-ssr-component');
	// 	MyComponent = module.default;
	});
	
	
	// if (process.browser) {
	// 	fetch('http://localhost:9019/organizations', {
	// 		method: 'POST',
	// 		headers: { 'Content-Type': 'application/json'},
	// 		body: JSON.stringify({name : "test2", description : "test2desc"})
	// 	})
	// 		.then(results => results.json())
	// 		.then(console.log);
	// }

	// var data = { name: "testtest", description: "testtestdesc" }
	// // var url = '/process/contact' // associated script = /src/routes/process/contact.js
	// var url = '/organizations';
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
	// .catch(err => {
	// 	// POST error: do something...
	// 	console.log('POST error', err.message)
	// })

	export let tree;
	// export let orgs;
	// export let units;
	// export let contexts;
	// export let schemas;

	import Button from 'sveltestrap/src/Button.svelte';
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

	let root = [
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
	];

	// root = tree;

	let activeSpec = true;
	let activeDesc = false;
	let activeOne = true;
	let activeTwo = false;

	let specification = "";
	let description = "";
</script>

<!-- {@debug orgs}
<p>{orgs[0].organizationId}</p>
<p>{orgs[0].name}</p>
{@debug orgs} -->


<Card>
	<CardHeader tag="h3">
		<!-- <FormGroup> -->
			<Label for="search">Search</Label>
			<Input type="search" name="search" id="search" placeholder="search..." />
		<!-- </FormGroup> -->

		<!-- reload button (if needed) -->
	</CardHeader>
	<CardBody>
		<Folder files={root} first={true}/>
	</CardBody>
</Card>


<div class="margin-top flex">
<Card>
		<div class="bottom-left">
			<ListGroup>
				<ListGroupItem active={activeOne} tag="button" action on:click={() => {activeOne = true; activeTwo = false;}}>0.0.1</ListGroupItem>
				<ListGroupItem active={activeTwo} tag="button" action on:click={() => {activeTwo = true; activeOne = false;}}>1.0.0</ListGroupItem>
			</ListGroup>
		</div>
</Card>

<div class="spacer"></div>

<div class="bottom-right">
<Card>
	
		<ListGroup class="d-flex flex-row w-75">
				
					<ListGroupItem active={activeSpec} tag="button" action on:click={() => {activeSpec = true; activeDesc = false;}}>Specification</ListGroupItem>
				
					<ListGroupItem active={activeDesc} tag="button" action on:click={() => {activeDesc = true; activeSpec = false;}}>Description</ListGroupItem>
				
		</ListGroup>
		{#if activeSpec}
			<ValidatedInput type="textarea" bind:value={specification}/>
			<div class="flex">
				<Button outline color="primary">
					<div class="flex">
						<Icon icon={mdiLabel}/> publish
					</div>
				</Button>
				<Button outline color="warning">
					<div class="flex">
						<Icon icon={mdiLabelOff}/> deprecate
					</div>
				</Button>
				<Button outline color="danger">
					<div class="flex">
						<Icon icon={mdiDelete}/> remove
					</div>
				</Button>
				<Button outline color="info">
					<div class="flex">
						<Icon icon={mdiSourcePull}/> source
					</div>
				</Button>
				<Button color="info">
					save specification
				</Button>
			</div>
		{:else}
			<ValidatedInput type="textarea" bind:value={description}/>
			<div class="flex">
				<Button color="success">preview</Button>
				<Button color="warning">revert</Button>
				<Button color="info">save description</Button>
			</div>
		{/if}
</Card>
</div>
</div>

<style>
	.margin-top {
		margin-top: 1rem;
	}
	.spacer {
		flex-grow: 1;
		display: block;
	}
	.flex {
		display: flex;
	}

	.bottom-right {
		width: 40rem;
	}
</style>