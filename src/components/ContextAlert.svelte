<script>
	import Alert from "sveltestrap/src/Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Choose a Context</h4>
		<p>Choose one of the Contexts inside the Tree-View.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Define a Context</h4>
		<p>When you have defined a context, it will be shown inside the Tree-View.</p>
		<p>
			<a href="/context" class="alert-link">Click here to define a Context</a>.
		</p>
	</Alert>
{/if}