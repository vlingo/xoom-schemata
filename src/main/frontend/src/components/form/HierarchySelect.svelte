<script>
	import { detailed} from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";
	import { Select } from 'svelte-materialify/src';

	export let label = "";
	export let storeOne;
	export let storeAll;
	export let arrayOfSelectables;
	export let containerClasses = "";

	function updateStore(e) {
		let value = e.detail;
		console.log(value);
		console.log("test");
		$storeOne = value ? $storeAll.find(obj => idReturner(obj) === value) : undefined;
		console.log($storeOne);
	}
</script>

<div class="flex-child {containerClasses}">
	<Select items={getHierarchySelectItemsFrom(arrayOfSelectables, $detailed)} value={$storeOne ? idReturner($storeOne) : ""} mandatory on:change={updateStore}>
		{label}
	</Select>
</div>

<style>
	.flex-child {
		/* flex: 0 0 50%; */
		padding: 12px;
	}
</style>
