<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

{#if notChosenAlert}
	<Alert>
		<div slot="heading">Choose a Unit</div>
		Choose a Unit in the tree.
	</Alert>
{:else}
	<Alert link={"unit"}>
		<div slot="heading">Define a Unit</div>
		When you have defined a unit, it will be shown inside the tree.
		<div slot="link-text">Click here to define a Unit.</div>
	</Alert>
{/if}