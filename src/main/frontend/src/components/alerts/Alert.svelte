<script>
	import { Alert } from 'svelte-materialify/src';
	export let name;
	export let link;
	$: headingText = link ? `Define ${name}` : `Choose ${name}`;
	$: paragraphText = link ? `When you have defined ${name}, it will be shown inside a tree right here.` : `Choose ${name} in the tree.`
	$: linkText = link ? `Click here to start by defining ${name}.` : ``
</script>


<Alert transitionOpts={{ duration: 250 }} class="info-color" outlined>
	<h4 class="alert-heading">
		<slot name="heading">
			{headingText}
		</slot>
	</h4>
	<p>
		<slot>
			{paragraphText}
		</slot>
	</p>
	{#if link}
		<p>
			<a href={link} class="alert-link">
				<slot name="link-text">
					{linkText}
				</slot>
			</a>
		</p>
	{/if}
</Alert>
