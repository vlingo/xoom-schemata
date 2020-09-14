<script context="module">
	export async function preload(page, session) {
		// `this.fetch` is a wrapper around `fetch` that allows you to make credentialled requests on both server and client
		var _this = this;

		let tree = [];

		async function fetchOrgsInto(tree) {
			const res = await _this.fetch('http://localhost:9019/organizations');
			const orgs = await res.json();
			for(const org of orgs) {
				tree.push(
					{
						type: 'organization',
						name: org.name,
						id: org.organizationId,
					}
				)
			}
		};
		await fetchOrgsInto(tree);
		// console.log("after orgs:", {tree});

		async function fetchUnitsInto(tree) {
			for(const org of tree) {
				const res = await _this.fetch(`http://localhost:9019/organizations/${org.id}/units`);
				const units = await res.json();
				for(const unit of units) {
					if(!org.files) org.files = [];
					org.files.push(
						{
							type: 'unit',
							name: unit.name,
							id: unit.unitId,
						}
					)
				}
			}
		};
		await fetchUnitsInto(tree);
		// console.log("after units:", {tree}, tree[0].files);

		async function fetchContextsInto(tree) {
			for(const org of tree) {
				if(org.files) {
					for(const unit of org.files) {
						const res = await _this.fetch(`http://localhost:9019/organizations/${org.id}/units/${unit.id}/contexts`);
						const contexts = await res.json();

						for(const context of contexts) {
							if(!unit.files) unit.files = [];
							unit.files.push(
								{
									type: 'context',
									name: context.namespace,
									id: context.contextId,
								}
							)
						}
					}
				}
			}
		};
		await fetchContextsInto(tree);
		// console.log("after contexts:", {tree}, tree[0].files, tree[0].files[0].files);


		return { tree };
	}
</script>
<script>
	import Nav from '../components/Nav.svelte';
	import { firstPage, organizationsStore } from '../stores';

	// export let tree;
	
	
	// if($firstPage) {
	// 	console.log({$firstPage}, "BEFORE");
	// 	$organizationsStore.push(...tree);
	// 	$firstPage = false;
	// 	console.log({$firstPage}, "AFTER");
	// }

</script>

<style>
	main {
		position: relative;
		max-width: 56em;
		min-width: 20em;
		background-color: white;
		padding: 2em;
		margin: 0 auto;
		box-sizing: border-box;
	}
</style>

<Nav/>

<main>
	<slot></slot>
</main>