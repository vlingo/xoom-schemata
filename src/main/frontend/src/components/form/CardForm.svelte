<script>
	import { mdiChevronLeft, mdiChevronRight } from '@mdi/js';
	import { createEventDispatcher } from 'svelte';
	import { Card, CardActions, CardText, CardTitle, Switch } from 'svelte-materialify/src';
	import { detailed } from '../../stores';
	import Button from './Button.svelte';
	import ButtonBar from './ButtonBar.svelte';

	export let title = "";
	export let linkToNext = "NEXT";
	export let prevLink = "";
	export let href = linkToNext && linkToNext.split(" ").length > 1 && linkToNext.split(" ")[1].toLowerCase(); //"CREATE UNIT" = unit
	export let showNewButton = true;
	const dispatch = createEventDispatcher();

	export let isDefineDisabled = false;
	export let isRedefineDisabled = false;
	export let isNextDisabled = false;

	export let defineMode = true;

	export let fullyQualified = "";

</script>

<Card class="vl-card pa-4 pa-md-8">
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
				{#if prevLink}
					<Button outlined color="primary" text="Prev" icon={mdiChevronLeft} href={prevLink}/>
				{/if}
				{#if showNewButton}
					<div class="mr-auto">
						<Button color="info" text="{ !defineMode ? `New ${title}` : `Redefine ${title}` }" on:click={() => dispatch("new")}/>
					</div>
				{/if}
				{#if !defineMode}
					<Button color="primary" text="Redefine" on:click={() => dispatch("redefine")} disabled={isRedefineDisabled}/>
				{:else}
					<Button color="primary" text="Define" on:click={() => dispatch("define")} disabled={isDefineDisabled}/>
				{/if}
				{#if !isNextDisabled && linkToNext}
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
