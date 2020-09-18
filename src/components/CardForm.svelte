<script>
	import { createEventDispatcher } from 'svelte';
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button as StrapButton } from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import Button from './Button.svelte';
	import ButtonBar from './ButtonBar.svelte';

	export let title = "";
	export let linkToNext = "NEXT";
	export let href = linkToNext.split(" ")[1].toLowerCase(); //"CREATE UNIT" = unit

	const dispatch = createEventDispatcher();

	export let isCreateDisabled;
	export let isUpdateDisabled;
	export let isNextDisabled;

</script>

<Card>
	<CardHeader tag="h3">{title}</CardHeader>
	<CardBody>
		<Form>
			<slot>

			</slot>
		</Form>
		
			<slot name="buttons">
				<ButtonBar>
					<div class="mr-auto">
						<Button color="info" text="NEW" on:click={() => dispatch("clear")}/> <!-- clear? -->
					</div>
					<Button color="primary" text="SAVE" on:click={() => dispatch("update")} disabled={isUpdateDisabled}/> <!-- update? -->
					<Button color="primary" text="CREATE" on:click={() => dispatch("create")} disabled={isCreateDisabled}/>
					<!-- disabled doesn't work on a-href, so just don't show the button when not needed: -->
					{#if !isNextDisabled}
						<Button color="primary" outline text={linkToNext} {href} disabled={isNextDisabled}/>
					{/if}
				</ButtonBar>
			</slot>
		
	</CardBody>
</Card>