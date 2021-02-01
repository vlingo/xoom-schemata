<script>
	import { Alert } from 'svelte-materialify/src';
	export let link = "";
</script>


<Alert transitionOpts={{ duration: 100 }} class="info-color" outlined>
	<h4 class="alert-heading"><slot name="heading"></slot></h4>
	<p><slot/></p>
	{#if link}
		<p><a href={link} class="alert-link"><slot name="link-text"></slot></a></p>
	{/if}
</Alert>