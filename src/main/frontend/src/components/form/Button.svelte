<script>
	import { Icon, Button } from 'svelte-materialify/src';
	import { createEventDispatcher } from 'svelte';
	import { goto } from '@sapper/app';

	const dispatch = createEventDispatcher();

	export let text = "";
	export let color = "primary";
	export let outlined = false;
	export let icon = "";
	export let href = "";
	export let disabled = false;

	function clicked() {
		if (href) {
			goto(href);
			return;
		} else {
			dispatch("click");
		}
	}

	$: btnClass = outlined ? color+"-color " + color+"-text" : disabled ? disabled : color+"-color ";

</script>

<Button class={"m-1 " + btnClass} {...$$restProps} disabled={disabled} on:click={clicked} {outlined}>
	<span>
		{#if icon}
			<Icon path={icon}/>
		{/if}
		{text}
	</span>
</Button>
