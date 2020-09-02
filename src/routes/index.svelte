


<script context="module">
	// export async function preload(page, session) {
	// 	// `this.fetch` is a wrapper around `fetch` that allows
	// 	// you to make credentialled requests on both
	// 	// server and client
	// 	const res = await this.fetch('http://localhost:9019/organizations');
	// 	const orgs = await res.json();

	// 	return { orgs };
	// }
</script>




<script>
	// export let orgs;

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