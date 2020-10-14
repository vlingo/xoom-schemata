<script>
	import { mdiChevronRight } from '@mdi/js';
	import { createEventDispatcher } from 'svelte';
	import { Card } from 'svelte-materialify/src';
	import CardText from 'svelte-materialify/src/components/Card/CardText.svelte';
	import CardTitle from 'svelte-materialify/src/components/Card/CardTitle.svelte';
import Switch from 'svelte-materialify/src/components/Switch';
	import { detailed } from '../../stores';
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
	<CardTitle>
		<span>{title}
			{#if fullyQualified}
				<code>{fullyQualified}</code>
			{/if}
		</span>
		{#if !(title==="Organization" && defineMode)}
			<span style="margin-left: auto; font-size: 1rem;"><Switch bind:checked={$detailed}>Details</Switch></span>
		{/if}
	</CardTitle>
	<CardText>
		<slot/>
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
	</CardText>
</Card>

<style>
	code {
		margin-left: 5rem;
	}
</style>