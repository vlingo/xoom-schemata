<script>
	import Alert from "sveltestrap/src/Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Choose a Unit</h4>
		<p>Choose one of the Units inside the Tree-View.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Define a Unit</h4>
		<p>When you have defined a unit, it will be shown inside the Tree-View.</p>
		<p>
			<a href="/unit" class="alert-link">Click here to define a Unit</a>.
		</p>
	</Alert>
{/if}