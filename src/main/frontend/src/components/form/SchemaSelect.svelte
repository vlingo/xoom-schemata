<script>
	import { Select } from "svelte-materialify/src";
	import { detailed, schemasStore, schemaStore } from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";

	export let compatibleSchemas = writable([]);
</script>

<Select class="flex-child folder-inset3" items={getHierarchySelectItemsFrom($compatibleSchemas, $detailed)}
	value={$schemaStore ? idReturner($schemaStore) : ""} mandatory
	on:change={(e) => $schemaStore = e.detail ? $schemasStore.find(obj => idReturner(obj) === e.detail) : undefined}>
	Schema
</Select>
