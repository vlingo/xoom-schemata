<script>
	import Alert from "sveltestrap/src/Alert.svelte";

	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading">Choose a Schema Version</h4>
		<p>Choose a Schema Version in the tree.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading">Define a Schema Version</h4>
		<p>When you have defined a schema version, it will be shown inside the tree.</p>
		<p>
			<a href="/schemaVersion" class="alert-link">Click here to define a Schema Version</a>.
		</p>
	</Alert>
{/if}