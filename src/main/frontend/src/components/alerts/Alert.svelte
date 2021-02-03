<script>
	import { Alert } from 'svelte-materialify/src';
	export let name;
	export let link;
	$: headingText = link ? `Define an ${name}` : `Choose an ${name}`;
	$: paragraphText = link ? `When you have defined an ${name.toLowerCase()}, it will be shown inside a tree right here.` : `Choose an ${name} in the tree.`
	$: linkText = link ? `Click here to start by defining an ${name}.` : ``
</script>


<Alert transitionOpts={{ duration: 100 }} class="info-color" outlined>
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