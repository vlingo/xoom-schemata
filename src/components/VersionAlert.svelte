<script>
	import Alert from "sveltestrap/src/Alert.svelte";

	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Choose a Schema Version</h4>
		<p>Choose one of the Schema Versions inside the Tree-View, or the left menu.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Define a Schema Version</h4>
		<p>When you have defined a schema version, it will be shown inside the Tree-View.</p>
		<p>
			<a href="/schemaVersion" class="alert-link">Click here to define a Schema Version</a>.
		</p>
	</Alert>
{/if}