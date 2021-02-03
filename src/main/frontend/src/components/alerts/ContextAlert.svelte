<script>
	import Alert from "./Alert.svelte";
	export let notChosenAlert = false;
</script>

<Alert name="Context" link={!notChosenAlert ? "context" : ""} />