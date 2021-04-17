<script>
	import Button from "./Button.svelte";
	import ButtonBar from "./ButtonBar.svelte";
	import { schemaStore, schemaVersionsStore } from '../../stores';

	export let currentVersion;

	const versionPattern = /(\d+)\.(\d+)\.(\d+)/;

	function clickedVersionButton(type) {
		if($schemaStore) {
			let versionsWithSameSchemaAsCurrent = $schemaVersionsStore.filter(v => v.schemaId === $schemaStore.schemaId);
			let highestVersion = versionsWithSameSchemaAsCurrent.map(v => v.currentVersion).sort(sortVersions).pop();
			let [, major, minor, patch] = versionPattern.exec(highestVersion);
			switch(type) {
				case "patch": ++patch; break;
				case "minor": ++minor; patch = 0; break;
				case "major": ++major; minor = 0; patch = 0; break;
			}
			currentVersion = `${major}.${minor}.${patch}`;
		}
	}
	//TODO: review edge-cases
	function sortVersions(a, b) {
		const [, majorA, minorA, patchA] = versionPattern.exec(a);
		const [, majorB, minorB, patchB] = versionPattern.exec(b);
		if(majorA > majorB) {
			return 1;
		}
		if(majorA == majorB && minorA > minorB) {
			return 1;
		}
		if(majorA == majorB && minorA == minorB && patchA > patchB) {
			return 1;
		}
		return 0;
	}
</script>

{#if $schemaStore && $schemaVersionsStore.find(v => v.schemaId === $schemaStore.schemaId)}
	<ButtonBar center>
		<Button color="error" text="New Major" on:click={() => clickedVersionButton("major")}/>
		<Button color="warning" text="New Minor" on:click={() => clickedVersionButton("minor")}/>
		<Button color="primary" text="New Patch" on:click={() => clickedVersionButton("patch")}/>
	</ButtonBar>
{/if}
