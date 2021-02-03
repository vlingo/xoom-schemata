<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="Unit" link={!notChosenAlert ? "unit" : ""} />