<script>
	import { Select } from "svelte-materialify/src";
	import { detailed, unitsStore, unitStore } from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";

	export let compatibleUnits = writable([]);
</script>

<Select class="flex-child folder-inset1" items={getHierarchySelectItemsFrom($compatibleUnits, $detailed)}
	value={$unitStore ? idReturner($unitStore) : ""} mandatory
	on:change={(e) => $unitStore = e.detail ? $unitsStore.find(obj => idReturner(obj) === e.detail) : undefined}>
	Unit
</Select>
