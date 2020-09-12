<script>
	import { createEventDispatcher } from 'svelte';
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button as StrapButton } from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import Button from './Button.svelte';
	import ButtonBar from './ButtonBar.svelte';

	export let title = "";
	export let next = "NEXT";

	const dispatch = createEventDispatcher();

</script>

<Card>
	<CardHeader tag="h3">{title}</CardHeader>
	<CardBody>
		<Form>
			<slot>

			</slot>
		</Form>
		<ButtonBar>
			<slot name="buttons">
				<Button color="info" text="NEW" on:click={() => dispatch("clear")}/> <!-- clear? -->
				<Button color="primary" text="SAVE" on:click={() => dispatch("update")}/> <!-- update? -->
				<Button color="primary" text="CREATE" on:click={() => dispatch("create")}/>
				<Button color="primary" outline text="CREATE {next}" href="{next.toLowerCase()}"/>
			</slot>
		</ButtonBar>
	</CardBody>
</Card>