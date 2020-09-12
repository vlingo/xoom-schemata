

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

	import { organizationsStore } from '../stores';
	import SchemataRepository from '../api/SchemataRepository';
	import { onMount } from 'svelte';
	// let MyComponent;
	onMount(async () => {
		// console.log(document.querySelector(".navbar-brand"));
		console.log("index");

		const orgs = await SchemataRepository.getOrganizations();
		console.log({orgs});
		// const newOrg = await SchemataRepository.createOrganization("test2", "test2desc");
		// console.log({newOrg});
		// const orgs2 = await SchemataRepository.getOrganizations();
		// console.log({orgs2});

	// 	const module = await import('my-non-ssr-component');
	// 	MyComponent = module.default;
	});
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

	root = $organizationsStore;

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
		<Folder files={root} first={true}/>
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