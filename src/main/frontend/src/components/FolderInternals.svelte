<script>
	import {mdiDelete, mdiLabel, mdiLabelOff, mdiPlaylistPlay} from '@mdi/js'
	import Clickable from "./Clickable.svelte";
	import Tooltip from "./Tooltip.svelte";
	import { getFileString } from "../utils";
	import Icon from "./Icon.svelte";
	import Badge from 'svelte-materialify/src/components/Badge';

	export let file;
	export let expandable = false;
	export let expanded = false;
	export let detailed = false;
</script>

<Tooltip tooltipText={file.type}>
	<Clickable {file} bind:selected={expanded}>
		{#if expandable}
			{#if expanded}▾{:else}▸{/if}
		{/if}

		<!-- this can also be done via the initial array in index.svelte -->
		<!-- {#if file.icon} <Icon icon={file.icon}/> {/if}-->
		{#if file.status == "Draft"}
			<Icon icon={mdiPlaylistPlay} />
		{:else if file.status == "Published"}
			<Icon icon={mdiLabel} />
		{:else if file.status == "Deprecated"}
			<Icon icon={mdiLabelOff} />
		{:else if file.status == "Removed"}
			<Icon icon={mdiDelete} />
		{/if}
		{getFileString(file, detailed)}
		<!-- <Badge class="{status.color}-color" value={status.text}/> -->
		{#if file.category}
			<Badge class="primary-color" value={file.category} offsetX={0} offsetY={0}/>
		{/if}
		{#if file.scope}
			<Badge class="primary-color" value={file.scope} offsetX={0} offsetY={0}/>
		{/if}
	</Clickable>
</Tooltip>