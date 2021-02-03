<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="Schema Version" link={!notChosenAlert ? "schemaVersion" : ""} />