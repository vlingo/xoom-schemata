<script>
	import { Card } from "svelte-materialify";
	import marked from 'marked';
	import DOMPurify from 'dompurify';

	export let description;
</script>

<Card disabled={!description} class="ma-2 pl-5 pt-2 pb-5 pr-2" style="min-height: 5rem">
	<div id="markdown-container">
		{#if description}
			{@html DOMPurify.sanitize(marked(description))}
		{:else}
			{@html marked("##### &#35;&#35;&#35;&#35; Write some &#95;_markdown_&#95; into &#42;&#42;**Description**&#42;&#42;")}
		{/if}
	</div>
</Card>
