<script>
	import { Select } from "svelte-materialify/src";
	import { detailed, organizationsStore, organizationStore } from "../../stores";
	import { getHierarchySelectItemsFrom, idReturner } from "../../utils";
</script>

<Select class="flex-child" items={getHierarchySelectItemsFrom($organizationsStore, $detailed)}
	value={$organizationStore ? idReturner($organizationStore) : ""} mandatory
	on:change={(e) => $organizationStore = e.detail ? $organizationsStore.find(obj => idReturner(obj) === e.detail) : undefined}>
	Organization
</Select>
