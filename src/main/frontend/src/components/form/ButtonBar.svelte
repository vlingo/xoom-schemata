<script>
	export let center = false;
</script>

<div class="button-bar {center? "align-self-center mt-3" : ""}">
	<slot/>
</div>

<style>
	.button-bar {
		display: flex;
		justify-content: flex-end;

		flex-wrap: wrap;
	}

	@media (min-width: 820px) {
		.button-bar {
			flex-wrap: nowrap;
		}
	}
</style>