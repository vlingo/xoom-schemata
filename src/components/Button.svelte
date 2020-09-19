<script>
	import StrapButton from "sveltestrap/src/Button.svelte";
	import Icon from "./Icon.svelte";

	export let text = "";
	export let color = "primary";
	export let outline = false;
	export let icon = "";
	export let href = "";
	export let disabled = false;

</script>



<StrapButton disabled={disabled} on:click class="mx-1" {outline} {color} {href}>
	<span>
		{#if icon}
			<Icon {icon}/>
		{/if}
		{text}
	</span>
</StrapButton>