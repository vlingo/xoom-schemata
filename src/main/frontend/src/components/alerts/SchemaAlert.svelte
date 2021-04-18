<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="a Schema" link={!notChosenAlert ? "schema" : ""} />
