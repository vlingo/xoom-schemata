<script>
	import { isMobile } from "../stores";
	export let tooltipText;
</script>

<span class="tooltip-css" on:click>
	<slot/>
	<div style={$isMobile ? "" : "z-index: 5;"} class="tooltiptext-css">{tooltipText}</div>
</span>

<style>
	.tooltip-css {
	  position: relative;
	  display: inline-block;
	}
	.tooltip-css .tooltiptext-css {
	  visibility: hidden;
	  width: 120px;
	  background-color: black;
	  color: #fff;
	  text-align: center;
	  padding: 5px 0;
	  border-radius: 6px;
	  position: absolute;
	  user-select: none;
	}
	.tooltip-css:hover .tooltiptext-css {
		visibility: visible;
	}
	.tooltip-css .tooltiptext-css {
		top: -5px;
		right: 105%;
	}
</style>