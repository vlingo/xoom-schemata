<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>


{#if notChosenAlert}
	<Alert>
		<div slot="heading">Choose a Schema</div>
		Choose a Schema in the tree.
	</Alert>
{:else}
	<Alert link={"schema"}>
		<div slot="heading">Define a Schema</div>
		When you have defined a schema, it will be shown inside the tree.
		<div slot="link-text">Click here to define a Schema.</div>
	</Alert>
{/if}