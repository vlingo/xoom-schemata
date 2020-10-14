<script>
	import Folder from '../components/Folder.svelte';
	import Button from '../components/form/Button.svelte';
	import OrganizationAlert from '../components/alerts/OrganizationAlert.svelte';
	import UnitAlert from '../components/alerts/UnitAlert.svelte';
	import ContextAlert from '../components/alerts/ContextAlert.svelte';
	import SchemaAlert from '../components/alerts/SchemaAlert.svelte';
	import VersionAlert from '../components/alerts/VersionAlert.svelte';
	import VersionContainer from '../components/VersionContainer.svelte';
	import ValidatedInput from '../components/form/ValidatedInput.svelte';
	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
import Card from 'svelte-materialify/src/components/Card';
import CardTitle from 'svelte-materialify/src/components/Card/CardTitle.svelte';
import CardText from 'svelte-materialify/src/components/Card/CardText.svelte';

	//could change to organizationId, unitId, etc.
	//also could be reduced to one big function which would reduce for-loops
	// ################
	// for(const org of tree) {
	// 	const compatibleUnits = ($unitsStore).find(u => u.organizationId == org.id);
	// 	if(compatibleUnits) org.files = [];
	// 	for(const unit of compatibleUnits) {
	// 		org.files.push(
	// 			{
	// 				type: 'unit',
	// 				name: unit.name,
	// 				description: unit.description,
	// 				id: unit.unitId
	// 			}
	// 		)
	// 		//you would need something like org.files[0], org.files[1], etc to do this, also still seems non-optimal
	// 	}
	// }
	// ################
	let tree = [];
	tree.files = [];
	// onMount(async () => {
	if(process.browser) {

		for(const org of $organizationsStore) {
			tree.files.push(
				{
					type: 'organization',
					name: org.name,
					description: org.description,
					id: org.organizationId
				}
			)
		}
		
		for(const org of tree.files) {
			const compatibleUnits = ($unitsStore).filter(u => u.organizationId == org.id);
			if(compatibleUnits.length>0) org.files = [];

			for(const unit of compatibleUnits) {
				org.files.push(
					{
						type: 'unit',
						name: unit.name,
						description: unit.description,
						id: unit.unitId
					}
				)
			}
		}
		
		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					const compatibleContexts = ($contextsStore).filter(c => c.unitId == unit.id);
					if(compatibleContexts.length>0) unit.files = [];

					for(const context of compatibleContexts) {
						unit.files.push(
							{
								type: 'context',
								namespace: context.namespace,
								description: context.description,
								id: context.contextId
							}
						)
					}
				}
			}
		}

		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					if(unit.files) {
						for(const context of unit.files) {
							const compatibleSchemas = ($schemasStore).filter(s => s.contextId == context.id);
							if(compatibleSchemas.length>0) context.files = [];

							for(const schema of compatibleSchemas) {
								context.files.push(
									{
										type: 'schema',
										name: schema.name,
										description: schema.description,
										category: schema.category,
										scope: schema.scope,
										id: schema.schemaId
									}
								)
							}
						}
					}
				}
			}
		}

		for(const org of tree.files) {
			if(org.files) {
				for(const unit of org.files) {
					if(unit.files) {
						for(const context of unit.files) {
							if(context.files) {
								for(const schema of context.files) {
									const compatibleSchemaVersions = ($schemaVersionsStore).filter(sv => sv.schemaId == schema.id);
									if(compatibleSchemaVersions.length>0) schema.files = [];
									
									for(const schemaVersion of compatibleSchemaVersions) {
										schema.files.push(
											{
												type: 'schemaVersion',
												status: schemaVersion.status,
												specification: schemaVersion.specification,
												description: schemaVersion.description,
												previous: schemaVersion.previousVersion,
												current: schemaVersion.currentVersion,
												id: schemaVersion.schemaVersionId
											}
										)
									}
								}
							}
						}
					}
				}
			}
		}

		// console.log(document);
	}
	
	let root = [];
	root = tree;
	console.log(root);

	function updateTreeWith(updated) {
		// the tree could also just be a map: root[orgId][unitId]...
		function replaceInTree(rootfiles, updated) {
			let idsFromOrgToVersion = [updated.organizationId, updated.unitId, updated.contextId, updated.schemaId, updated.schemaVersionId];
			function recursiveSearch(array, iteration) {
				const idx = array.findIndex(el => el.id == idsFromOrgToVersion[iteration]);
				//end
				if(iteration >= idsFromOrgToVersion.length-1) {
					console.log(array, idx);
					array[idx] =
					{
						type: 'schemaVersion',
						status: updated.status,
						specification: updated.specification,
						description: updated.description,
						previous: updated.previousVersion,
						current: updated.currentVersion,
						id: updated.schemaVersionId
					};
					return
				}
				recursiveSearch(array[idx].files, ++iteration);
			}
			//start
			recursiveSearch(rootfiles, 0);
		}
		replaceInTree(root.files, updated);
		root = root;
	}

</script>

<svelte:head>
	<title>Home</title>
</svelte:head>

<Card>
	<CardTitle>
		Home
		{#if root.files.length > 0}
			<Button on:click={() => $detailed = !($detailed)} style="margin-left: auto" text={"Show Details"}/>
		{/if}
	</CardTitle>

	{#if root.files.length < 1}
		<OrganizationAlert/>
	{:else}
		<ValidatedInput type="search" name="search" id="search" placeholder="Search..."/>

		<CardText>
			<Folder detailed={$detailed} file={root} first={true}/>
		</CardText>
		
		{#if $unitsStore.length < 1}
			<UnitAlert/>
		{:else}
			{#if $contextsStore.length < 1}
				<ContextAlert/>
			{:else}
				{#if $schemasStore.length < 1}
					<SchemaAlert/>
				{:else}
					{#if $schemaVersionsStore.length < 1}
						<VersionAlert/>
					{:else}
						{#if !$organizationStore}
							<OrganizationAlert notChosenAlert/>
						{:else if !$unitStore}
							<UnitAlert notChosenAlert/>
						{:else if !$contextStore}
							<ContextAlert notChosenAlert/>
						{:else if !$schemaStore}
							<SchemaAlert notChosenAlert/>
						{:else if !$schemaVersionStore}
							<VersionAlert notChosenAlert/>
						{/if}
					{/if}
				{/if}
			{/if}
		{/if}
	{/if}
</Card>

<VersionContainer on:versionChanged={(v) => updateTreeWith(v.detail)}/>