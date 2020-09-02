<script>
	import { createEventDispatcher } from 'svelte';
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';

	export let title = "";
	export let next = "next";

	const dispatch = createEventDispatcher();

	let id;
	let name;
	let description;
	let nameValid = false;
	let nameInvalid = false;
	let descriptionValid = false;
	let descriptionInvalid = false;
</script>

<Card>
	<CardHeader tag="h3">{title}</CardHeader>
	<CardBody>
		<Form>
			<slot>

			</slot>
		</Form>
		<Button color="primary" outline on:click={() => dispatch("clear")}>new (clear)</Button>
		<Button color="primary" outline on:click={dispatch("update")}>save (update)</Button>
		<Button color="primary" outline on:click={dispatch("create")}>create</Button>
		<Button color="primary" outline href="{next}">create {next}</Button>
	</CardBody>
</Card>