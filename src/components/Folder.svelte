<script>
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiPlaylistPlay} from '@mdi/js'

	import { getFileString, isObjectInAStore } from "../utils";
	import Tooltip from "./Tooltip.svelte";
	import Icon from "./Icon.svelte";
	import Clickable from './Clickable.svelte';
	import { contextStore, organizationStore, schemaStore, schemaVersionStore, unitStore } from '../stores';
	export let first = false;

	export let file;

	let expanded = isObjectInAStore(file);
	
	$: if(($organizationStore || $unitStore || $contextStore || $schemaStore || $schemaVersionStore) && (isObjectInAStore(file))) {
		expanded = true;
	} else {
		expanded = false;
	}

	// function toggle() {
	// 	expanded = !expanded;
	// }
</script>


{#if !first}
<span class:expanded>
	<Tooltip tooltipText={file.type}> <!-- on:click={toggle} -->
		<Clickable {file} {expanded}>
			{#if expanded}▾{:else}▸{/if}
			{getFileString(file)}
		</Clickable>
	</Tooltip>
</span>
{/if}

{#if expanded || first}
	<ul>
		{#each file.files as file}
			<li>
				{#if file.files != undefined}
					<svelte:self {file}/>
				{:else}
					<!-- original: <span>{file.name}</span>   style="background-image: url(tutorial/icons/{type}.svg) -->
					<!-- this is the leaf-element, we could style it differently-->
					<span>
						<Tooltip tooltipText={file.type}>
							<Clickable {file}>
								<!-- this can also be done via the initial array in index.svelte -->
								<!-- {#if file.icon} <Icon icon={file.icon}/> {/if}-->
								{#if file.status == "Draft"}
									<Icon icon={mdiPlaylistPlay} />
								{:else if file.status == "Published"}
									<Icon icon={mdiLabel} />
								{:else if file.status == "Deprecated"}
									<Icon icon={mdiLabelOff} />
								{:else if file.status == "Removed"}
									<Icon icon={mdiDelete} />
								{/if}
								{getFileString(file)}
							</Clickable>
						</Tooltip>
					</span>
				{/if}
			</li>
		{/each}
	</ul>
{/if}


<style>
	.expanded {
		/* background-image: url(tutorial/icons/folder-open.svg); */
	}
	span {
		/* padding: 0 0 0 1.5em; */
		/* background: url(tutorial/icons/folder.svg) 0 0.1em no-repeat; */
		background-size: 1em 1em;
		font-weight: bold;
		cursor: pointer;
		
		/* make unmarkable */
		user-select: none;
	}

	ul {
		padding: 0.2em 0 0 0.5em;
		margin: 0 0 0 2em;
		list-style: none;
		border-left: 1px solid #eee;
	}

	li {
		padding: 0.2em 0;
	}
</style>