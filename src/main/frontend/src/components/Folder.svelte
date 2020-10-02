<script>
	import { isObjectInAStore } from "../utils";
	import { contextStore, organizationStore, schemaStore, schemaVersionStore, unitStore } from '../stores';
	import FolderInternals from './FolderInternals.svelte';
	export let first = false;

	export let file;
	export let detailed = false;

	let expanded = isObjectInAStore(file);
	
	$: if(($organizationStore || $unitStore || $contextStore || $schemaStore || $schemaVersionStore) && (isObjectInAStore(file))) {
		expanded = true;
	} else {
		expanded = false;
	}
</script>


{#if !first}
<span class:expanded>
	<FolderInternals {file} expandable={true} expanded={expanded} detailed={detailed}/>
</span>
{/if}

{#if expanded || first}
	<ul>
		{#each file.files as file}
			<li>
				{#if file.files != undefined}
					<svelte:self {file} detailed={detailed}/>
				{:else}
					<!-- original: <span>{file.name}</span>   style="background-image: url(tutorial/icons/{type}.svg) -->
					<!-- this is the leaf-element, we could style it differently-->
					<span>
						<FolderInternals {file} expandable={false} detailed={detailed}/>
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