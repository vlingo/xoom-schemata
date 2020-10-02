<script>
	import ListGroupItem from "sveltestrap/src/ListGroupItem.svelte";
	import { schemaVersionStore } from '../stores';

	export let version;

	function chooseThis() {
		$schemaVersionStore = version;
	}

	$: active = ($schemaVersionStore.schemaVersionId == version.schemaVersionId);
</script>


<ListGroupItem active={active} tag="button" action on:click={chooseThis}>
	<slot/>
</ListGroupItem>