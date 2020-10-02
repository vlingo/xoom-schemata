

<div class="button-bar">
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