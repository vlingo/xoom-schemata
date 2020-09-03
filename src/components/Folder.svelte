<script>
	// import File from './File.svelte';
	export let first = false;

	export let name = "";
	export let files = [];

	//supress browser warnings
	export let type = "";
	type = type;

	let expanded = false;

	function toggle() {
		expanded = !expanded;
	}
</script>

<style>
	span {
		/* padding: 0 0 0 1.5em; */
		/* background: url(tutorial/icons/folder.svg) 0 0.1em no-repeat; */
		background-size: 1em 1em;
		font-weight: bold;
		cursor: pointer;
		
		/* make unmarkable */
		user-select: none;
	}

	.expanded {
		/* background-image: url(tutorial/icons/folder-open.svg); */
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




	/* Tooltip container */
	.tooltip-css {
	  position: relative;
	  display: inline-block;
	}
	
	/* Tooltip text */
	.tooltip-css .tooltiptext-css {
	  visibility: hidden;
	  width: 120px;
	  background-color: black;
	  color: #fff;
	  text-align: center;
	  padding: 5px 0;
	  border-radius: 6px;
	  
	  /* Position the tooltip text - see examples below! */
	  position: absolute;
	  z-index: 1;
	  /* make unmarkable */
	  user-select: none;
	}
	
	/* Show the tooltip text when you mouse over the tooltip container */
	.tooltip-css:hover .tooltiptext-css {
		visibility: visible;
	}
	.tooltip-css .tooltiptext-css {
		top: -5px;
		right: 105%;
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

	<span class="tooltip-css" class:expanded on:click={toggle}>
		{#if expanded}▾{:else}▸{/if}{name}
		<div class="tooltiptext-css">{type}</div>
	</span>

	{#if expanded}
		<ul>
			<!-- {@debug files} -->
			{#each files as file}
				<li>
					{#if file.files != undefined}
					<!-- {@debug file} -->
						<svelte:self {...file}/>
					{:else}
						<!-- <span>{file.name}</span>   style="background-image: url(tutorial/icons/{type}.svg) -->
						<span class="tooltip-css">{file.name}
							<div class="tooltiptext-css">{file.type}</div>
						</span>
					{/if}
				</li>
			{/each}
		</ul>
	{/if}
{/if}