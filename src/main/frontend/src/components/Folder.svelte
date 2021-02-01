<script>
	import { isEmpty, isObjectInAStore } from "../utils";
	import { contextStore, organizationStore, schemaStore, schemaVersionStore, unitStore } from '../stores';
	import FolderInternals from './FolderInternals.svelte';
	export let first = false;

	export let file;
	export let detailed = false;

	$: expanded = isObjectInAStore(file, $contextStore, $organizationStore, $schemaStore, $schemaVersionStore, $unitStore);
</script>


{#if !first}
	<span class:expanded>
		<FolderInternals {file} expandable bind:expanded {detailed}/>
	</span>
{/if}

{#if expanded || first}
	<ul>
		{#each Object.values(file.files) as file}
			<li>
				{#if !isEmpty(file.files)}
					<svelte:self {file} {detailed}/>
				{:else}
					<!-- this is the leaf-element, we could style it differently-->
					<span>
						<FolderInternals {file} {detailed}/>
					</span>
				{/if}
			</li>
		{/each}
	</ul>
{/if}


<style>
	span {
		font-weight: bold;
		cursor: pointer;
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