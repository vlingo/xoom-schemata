<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="a Unit" link={!notChosenAlert ? "unit" : ""} />
