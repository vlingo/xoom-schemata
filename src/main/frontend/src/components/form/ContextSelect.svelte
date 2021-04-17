<script>
	import { Select } from "svelte-materialify/src";
	import { detailed, contextsStore, contextStore } from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";

	export let compatibleContexts = writable([]);
</script>

<Select class="flex-child folder-inset2" items={getHierarchySelectItemsFrom($compatibleContexts, $detailed)}
	value={$contextStore ? idReturner($contextStore) : ""} mandatory
	on:change={(e) => $contextStore = e.detail ? $contextsStore.find(obj => idReturner(obj) === e.detail) : undefined}>
	Context
</Select>
