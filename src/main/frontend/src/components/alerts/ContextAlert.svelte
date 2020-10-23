<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert>
		<div slot="heading">Choose a Context</div>
		Choose a Context in the tree.
	</Alert>
{:else}
	<Alert link={"context"}>
		<div slot="heading">Define a Context</div>
		When you have defined a context, it will be shown inside the tree.
		<div slot="link-text">Click here to define a Context.</div>
	</Alert>
{/if}