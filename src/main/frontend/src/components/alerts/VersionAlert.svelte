<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert>
		<div slot="heading">Choose a Schema Version</div>
		Choose a Schema Version in the tree.
	</Alert>
{:else}
	<Alert link={"schemaVersion"}>
		<div slot="heading">Define a Schema Version</div>
		When you have defined a schema version, it will be shown inside the tree.
		<div slot="link-text">Click here to define a Schema Version.</div>
	</Alert>
{/if}