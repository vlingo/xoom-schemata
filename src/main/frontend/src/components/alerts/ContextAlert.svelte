<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="a Context" link={!notChosenAlert ? "context" : ""} />
