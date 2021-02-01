<script>
	import { mdiChevronRight } from '@mdi/js';
	import { createEventDispatcher } from 'svelte';
	import { Card, CardActions, CardText, CardTitle, Switch } from 'svelte-materialify/src';
	import { detailed } from '../../stores';
	import Button from './Button.svelte';
	import ButtonBar from './ButtonBar.svelte';

	export let title = "";
	export let linkToNext = "NEXT";
	export let href = linkToNext.split(" ")[1].toLowerCase(); //"CREATE UNIT" = unit

	const dispatch = createEventDispatcher();

	export let isDefineDisabled = false;
	export let isRedefineDisabled = false;
	export let isNextDisabled = false;

	export let defineMode = true;

	export let fullyQualified = "";

</script>

<Card class="pa-4 pa-md-8">
	<CardTitle class="pa-0">
		<h5>{title}
			{#if fullyQualified}
				<code>{fullyQualified}</code>
			{/if}
		</h5>
		{#if !(title==="Organization" && defineMode)}
			<span style="margin-left: auto; font-size: 1rem;"><Switch bind:checked={$detailed}>Details</Switch></span>
		{/if}
	</CardTitle>
	<CardText class="pa-0 mt-6 mb-6">
		<slot/>
	</CardText>
	<CardActions class="pa-0">
		<slot name="buttons">
			<ButtonBar>
				<div class="mr-auto">
					<Button color="info" text="New {title}" on:click={() => dispatch("new")}/>
				</div>
				{#if !defineMode}
					<Button color="primary" text="Redefine" on:click={() => dispatch("redefine")} disabled={isRedefineDisabled}/>
				{:else}
					<Button color="primary" text="Define" on:click={() => dispatch("define")} disabled={isDefineDisabled}/>
				{/if}
				<!-- disabled doesn't work on a-href, so just don't show the button when not needed: -->
				{#if !isNextDisabled}
					<Button color="primary" icon={mdiChevronRight} outline text={linkToNext} {href} disabled={isNextDisabled}/>
				{/if}
			</ButtonBar>
		</slot>
	</CardActions>
</Card>

<style>
	h5 {
		font-weight: bold;
	}
	code {
		margin-left: 5rem;
	}
</style>