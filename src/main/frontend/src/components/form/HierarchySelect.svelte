<script>
	import { idReturner } from "../../utils";
	import { Select } from 'svelte-materialify/src';

	export let label = "";
	export let storeOne;
	export let storeAll;
	export let items;
	export let containerClasses = "";

	function updateStore(e) {
		$storeOne = e.detail ? $storeAll.find(obj => idReturner(obj) === e.detail) : undefined;
	}
</script>

<div class="flex-child {containerClasses}">
	<Select {items} value={$storeOne ? idReturner($storeOne) : ""} mandatory on:change={updateStore}>
		{label}
	</Select>
</div>

<style>
	.flex-child {
		/* flex: 0 0 50%; */
		padding: 12px;
	}
</style>
