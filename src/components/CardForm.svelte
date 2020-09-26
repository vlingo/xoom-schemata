<script>
	import { mdiChevronRight } from '@mdi/js';
	import { createEventDispatcher } from 'svelte';
	import { Card, CardBody, Form} from 'sveltestrap/src';
	import CardHeader from 'sveltestrap/src/CardHeader.svelte';
	import { detailed } from '../stores';
	import Button from './Button.svelte';
	import ButtonBar from './ButtonBar.svelte';

	export let title = "";
	export let linkToNext = "NEXT";
	export let href = linkToNext.split(" ")[1].toLowerCase(); //"CREATE UNIT" = unit

	const dispatch = createEventDispatcher();

	export let isDefineDisabled = false;
	export let isSaveDisabled = false;
	export let isNextDisabled = false;

	export let defineMode = true;

	export let fullyQualified = "";

</script>

<Card>
	<CardHeader tag="h3">
		<span>{title}
			{#if fullyQualified}
				<code>{fullyQualified}</code>
			{/if}
		</span>
		<Button on:click={() => $detailed = !($detailed)} style="float: right" text={"Show Details"}/>
	</CardHeader>
	<CardBody>
		<Form>
			<slot>

			</slot>
		</Form>
		
			<slot name="buttons">
				<ButtonBar>
					<div class="mr-auto">
						<Button color="info" text="New {title}" on:click={() => dispatch("new")}/>
					</div>
					{#if !defineMode}
						<Button color="primary" text="Save" on:click={() => dispatch("save")} disabled={isSaveDisabled}/>
					{:else}
						<Button color="primary" text="Define" on:click={() => dispatch("define")} disabled={isDefineDisabled}/>
					{/if}
					<!-- disabled doesn't work on a-href, so just don't show the button when not needed: -->
					{#if !isNextDisabled}
						<Button color="primary" icon={mdiChevronRight} outline text={linkToNext} {href} disabled={isNextDisabled}/>
					{/if}
				</ButtonBar>
			</slot>
		
	</CardBody>
</Card>

<style>
	code {
		margin-left: 5rem;
	}
</style>