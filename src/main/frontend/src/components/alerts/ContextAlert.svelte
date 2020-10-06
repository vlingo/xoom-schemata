<script>
	import Alert from "sveltestrap/src/Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading">Choose a Context</h4>
		<p>Choose a Context in the tree.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading">Define a Context</h4>
		<p>When you have defined a context, it will be shown inside the tree.</p>
		<p>
			<a href="context" class="alert-link">Click here to define a Context</a>.
		</p>
	</Alert>
{/if}