<script>
	import Alert from "sveltestrap/src/Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert color="info">
		<h4 class="alert-heading">Choose an Organization</h4>
		<p>Choose an Organization in the tree.</p>
	</Alert>
{:else}
	<Alert color="info">
		<h4 class="alert-heading">Define an Organization</h4>
		<p>When you have defined an organization, it will be shown inside a tree right here.</p>
		<p>
			<a href="/organization" class="alert-link">Click here to start by defining an Organization</a>.
		</p>
	</Alert>
{/if}