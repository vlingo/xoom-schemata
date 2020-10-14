<script>
	import { Icon } from 'svelte-materialify/src';
	import MatButton from 'svelte-materialify/src/components/Button';
	// import StrapButton from "sveltestrap/src/Button.svelte";
	// import Icon from "./Icon.svelte";

	export let text = "";
	export let color = "primary";
	export let outlined = false;
	export let icon = "";
	export let href = "";
	export let disabled = false;

	let btnClass = outlined ? color+"-color " + color+"-text" : color+"-color ";
</script>


{#if !href}
<MatButton class={"m-1 " + btnClass} {...$$restProps} disabled={disabled} on:click {outlined}>
	<span>
		{#if icon}
			<Icon path={icon}/>
		{/if}
		{text}
	</span>
</MatButton>
{:else}
	<a href={href} rel="noopener">
		<MatButton class={"m-1 " + btnClass} {...$$restProps} disabled={disabled} on:click {outlined}>
			<span>
				{#if icon}
					<Icon path={icon}/>
				{/if}
				{text}
			</span>
		</MatButton>
	</a>
{/if}