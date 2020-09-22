<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import ListGroupItem from "sveltestrap/src/ListGroupItem.svelte";

	let item;

	function chooseThis() {
		$current = item;
	}
	
	$: active = (item == $current);
</script>


<ListGroupItem bind:this={item} active={active} tag="button" action on:click={chooseThis}>
	<slot/>
</ListGroupItem>