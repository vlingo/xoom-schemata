<script>
	// import File from './File.svelte';
	export let first = false;

	export let name = "";
	export let files = [];

	//supress browser warnings
	export let type = "";
	type = type;

	let expanded = true;

	function toggle() {
		expanded = !expanded;
	}
</script>

<style>
	span {
		padding: 0 0 0 1.5em;
		/* background: url(tutorial/icons/folder.svg) 0 0.1em no-repeat; */
		background-size: 1em 1em;
		font-weight: bold;
		cursor: pointer;
	}

	.expanded {
		/* background-image: url(tutorial/icons/folder-open.svg); */
	}

	ul {
		padding: 0.2em 0 0 0.5em;
		margin: 0 0 0 0.5em;
		list-style: none;
		border-left: 1px solid #eee;
	}

	li {
		padding: 0.2em 0;
	}
</style>





{#if first}
<ul>
	{#each files as file}
		<li>
			{#if file.files != undefined}
				<!-- {@debug file} -->
				<svelte:self {...file}/>
			{:else}
				<span>{file.name}</span> <!--  style="background-image: url(tutorial/icons/{type}.svg) -->
			{/if}
		</li>
	{/each}
</ul>
{:else}

<span class:expanded on:click={toggle}>{name}</span>

{#if expanded}
	<ul>
		<!-- {@debug files} -->
		{#each files as file}
			<li>
				{#if file.files != undefined}
				<!-- {@debug file} -->
					<svelte:self {...file}/>
				{:else}
					<span>{file.name}</span> <!--  style="background-image: url(tutorial/icons/{type}.svg) -->
				{/if}
			</li>
		{/each}
	</ul>
{/if}
{/if}