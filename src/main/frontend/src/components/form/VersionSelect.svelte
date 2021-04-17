<script>
	import { Select } from "svelte-materialify/src";
	import { detailed, schemaVersionsStore, schemaVersionStore } from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";

	export let compatibleVersions = writable([]);
</script>

<Select class="flex-child folder-inset4" items={getHierarchySelectItemsFrom($compatibleVersions, $detailed)}
	value={$schemaVersionStore ? idReturner($schemaVersionStore) : ""} mandatory
	on:change={(e) => $schemaVersionStore = e.detail ? $schemaVersionsStore.find(obj => idReturner(obj) === e.detail) : undefined}>
	Schema Version
</Select>
