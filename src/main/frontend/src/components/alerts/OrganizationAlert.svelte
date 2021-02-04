<script>
	import Alert from './Alert.svelte';
	export let notChosenAlert = false;
</script>

<Alert name="an Organization" link={!notChosenAlert ? "organization" : ""} />