<script>
	import { detailed} from "../../stores";
	import { changedSelect, idReturner, stringReturner } from "../../utils";
	import { Select, ListItem } from 'svelte-materialify/src';

	export let label = "";
	export let storeOne;
	export let storeAll;
	export let arrayOfSelectables;
	export let containerClasses = "flex-child";

	$: {changedSelected(value)};
	function changedSelected(selected) {
		$storeOne = selected ? ($storeAll).find(obj => idReturner(obj) == selected[0]) : undefined;
	}

	$: {changedSelectables(arrayOfSelectables, $detailed)};
	function changedSelectables(arrayOfSelectables, detailed) {
		items = changedSelect(arrayOfSelectables, detailed);
		value = [items[items.length - 1]];
	}

	const getTextFromId = (val) => typeof val[0] === 'string' ? stringReturner($storeAll.find(element => idReturner(element) == val[0]), $detailed) : "";

	let items;
	let value;

	//init selected
	let first = true;
	$: if(first && items) {
		value = [items[items.length - 1]];
		first = false;
	}
</script>

<!-- {#if $storeAll} -->
	<div class={containerClasses}>
		<Select {items} bind:value mandatory format={getTextFromId}>
			<span let:item slot="item">
				<ListItem value={item.id}>
					{item.text}
				</ListItem>
			</span>
			{label}
		</Select>
	</div>
<!-- {:else}
	<Select {items} bind:value mandatory>{label}</Select>
{/if} -->

<style>
	.flex-child {
		flex: 0 0 50%;
		padding: 12px;
	}
</style>
