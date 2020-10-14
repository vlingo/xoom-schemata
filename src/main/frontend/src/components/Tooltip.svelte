<script>
	export let tooltipText;
</script>

<span class="tooltip-css" on:click>
	<slot/>
	<div class="tooltiptext-css">{tooltipText}</div>
</span>

<style>
	/* Tooltip container */
	.tooltip-css {
	  position: relative;
	  display: inline-block;
	  z-index: 5;
	}
	
	/* Tooltip text */
	.tooltip-css .tooltiptext-css {
	  visibility: hidden;
	  width: 120px;
	  background-color: black;
	  color: #fff;
	  text-align: center;
	  padding: 5px 0;
	  border-radius: 6px;
	  
	  /* Position the tooltip text */
	  position: absolute;
	  
	  /* make unmarkable */
	  user-select: none;
	}
	
	/* Show the tooltip text when you mouse over the tooltip container */
	.tooltip-css:hover .tooltiptext-css {
		visibility: visible;
	}
	.tooltip-css .tooltiptext-css {
		top: -5px;
		right: 105%;
	}
</style>