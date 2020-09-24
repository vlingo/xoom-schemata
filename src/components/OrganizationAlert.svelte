<script>
	import Alert from "sveltestrap/src/Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Choose an Organization</h4>
		<p>Choose one of the Organizations inside the Tree-View.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading text-capitalize">Define an Organization</h4>
		<p>When you have defined an organization, it will be shown inside a Tree-View right here.</p>
		<p>
			<a href="/organization" class="alert-link">Click here to start by defining an Organization</a>.
		</p>
	</Alert>
{/if}