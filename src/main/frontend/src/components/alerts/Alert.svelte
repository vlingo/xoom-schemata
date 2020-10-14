<script>
	// import Alert from "sveltestrap/src/Alert.svelte";
	import Alert from 'svelte-materialify/src/components/Alert';
	export let link = "";
</script>


<Alert class="info-color" outlined>
	<h4 class="alert-heading"><slot name="heading"></slot></h4>
	<p><slot/></p>
	{#if link}
		<p><a href={link} class="alert-link"><slot name="link-text"></slot></a></p>
	{/if}
</Alert>