<script>
	import Alert from './Alert.svelte';
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert>
		<div slot="heading">Choose an Organization</div>
		Choose an Organization in the tree.
	</Alert>
{:else}
	<Alert link={"organization"}>
		<div slot="heading">Define an Organization</div>
		When you have defined an organization, it will be shown inside a tree right here.
		<div slot="link-text">Click here to start by defining an Organization.</div>
	</Alert>
{/if}