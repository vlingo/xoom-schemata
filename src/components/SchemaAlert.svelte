<script>
	import Alert from "sveltestrap/src/Alert.svelte";

	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Choose a Schema</h4>
		<p>Choose one of the Schemas inside the Tree-View.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Define a Schema</h4>
		<p>When you have defined a schema, it will be shown inside the Tree-View.</p>
		<p>
			<a href="/schema" class="alert-link">Click here to define a Schema</a>.
		</p>
	</Alert>
{/if}