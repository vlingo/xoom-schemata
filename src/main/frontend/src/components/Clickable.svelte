<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore } from "../utils";
	import { fade } from 'svelte/transition';
	
	export let file;
	export let selected;
	let item;

	$: if(!isObjectInAStore(file)) deAdjustStoresTo(file.type);
	
	function chooseThis() {
		if(!selected) {
			adjustStoresTo(file);
			$current = item;
		} else {
			deAdjustStoresTo(file.type);
		}
	}

	$: active = (item == $current);
</script>

<div transition:fade={{ duration: 100 }} bind:this={item} class:active={active} class:notActive={!active} on:click={chooseThis}>
	<slot/>
</div>


<style>
	.active {
		font-weight: bold;
		/* background-color: var(--vlingo-color1);  only do this on the schemaVersion*/
	}
	.notActive {
		font-weight: lighter;
	}
</style>