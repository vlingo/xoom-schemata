<script>
	import Folder from '../components/Folder.svelte';
	import OrganizationAlert from '../components/alerts/OrganizationAlert.svelte';
	import UnitAlert from '../components/alerts/UnitAlert.svelte';
	import ContextAlert from '../components/alerts/ContextAlert.svelte';
	import SchemaAlert from '../components/alerts/SchemaAlert.svelte';
	import VersionAlert from '../components/alerts/VersionAlert.svelte';
	import VersionContainer from '../components/VersionContainer.svelte';
	import Card from 'svelte-materialify/src/components/Card';
	import CardTitle from 'svelte-materialify/src/components/Card/CardTitle.svelte';
	import Switch from 'svelte-materialify/src/components/Switch';
	import { contextsStore, contextStore, detailed, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { isEmpty } from '../utils';

	// let root = { files: {
	// 	"org1" : {
	// 		type: 'organization',
	// 		name: "org.name",
	// 		description: "org.description",
	// 		id: "org.organizationId",
	// 		files: {
	// 			"unit1": {
	// 				type: 'unit',
	// 				name: "unit.name",
	// 				description: "unit.description",
	// 				id: "unit.unitId",
	// 				files: {
	// 					"context1": {
	// 						type: 'context',
	// 						namespace: "context.namespace",
	// 						description: "context.description",
	// 						id: "context.contextId",
	// 						files: {
	// 							"schema1" : {
	// 								type: 'schema',
	// 								name: "schema.name",
	// 								description: "schema.description",
	// 								category: "schema.category",
	// 								scope: "schema.scope",
	// 								id: "schema.schemaId",
	// 								files: {
	// 									"1.0.0" : {
	// 										type: 'schemaVersion',
	// 										status: "schemaVersion.status",
	// 										specification: "schemaVersion.specification",
	// 										description: "schemaVersion.description",
	// 										previous: "schemaVersion.previousVersion",
	// 										current: "schemaVersion.currentVersion",
	// 										id: "schemaVersion.schemaVersionId"
	// 									},
	// 									"2.0.0" : {
	// 										type: 'schemaVersion',
	// 										status: "schemaVersion.status2",
	// 										specification: "schemaVersion.specification2",
	// 										description: "schemaVersion.description2",
	// 										previous: "schemaVersion.previousVersion2",
	// 										current: "schemaVersion.currentVersion2",
	// 										id: "schemaVersion.schemaVersionId2"
	// 									}
	// 								}
	// 							}
	// 						}
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}
	// }}

	const getTree = () => {
		const root = {};
		root.files = {};
		for(const org of $organizationsStore) {
			root.files[org.organizationId] = {
				type: 'organization',
				name: org.name,
				description: org.description,
				id: org.organizationId
			}
			const compatibleUnits = ($unitsStore).filter(u => u.organizationId == org.organizationId);
			root.files[org.organizationId].files = {};
			for(const unit of compatibleUnits) {
				root.files[org.organizationId].files[unit.unitId] = {
					type: 'unit',
					name: unit.name,
					description: unit.description,
					id: unit.unitId
				}
				const compatibleContexts = ($contextsStore).filter(c => c.unitId == unit.unitId);
				root.files[org.organizationId].files[unit.unitId].files = {};
				for(const context of compatibleContexts) {
					root.files[org.organizationId].files[unit.unitId].files[context.contextId] = {
						type: 'context',
						namespace: context.namespace,
						description: context.description,
						id: context.contextId
					}
					const compatibleSchemas = ($schemasStore).filter(s => s.contextId == context.contextId);
					root.files[org.organizationId].files[unit.unitId].files[context.contextId].files = {};
					for(const schema of compatibleSchemas) {
						root.files[org.organizationId].files[unit.unitId].files[context.contextId].files[schema.schemaId] = {
							type: 'schema',
							name: schema.name,
							description: schema.description,
							category: schema.category,
							scope: schema.scope,
							id: schema.schemaId
						}
						const compatibleSchemaVersions = ($schemaVersionsStore).filter(sv => sv.schemaId == schema.schemaId);
						root.files[org.organizationId].files[unit.unitId].files[context.contextId].files[schema.schemaId].files = {};
						for(const schemaVersion of compatibleSchemaVersions) {
							root.files[org.organizationId].files[unit.unitId].files[context.contextId].files[schema.schemaId].files[schemaVersion.schemaVersionId] = {
								type: 'schemaVersion',
								status: schemaVersion.status,
								specification: schemaVersion.specification,
								description: schemaVersion.description,
								previous: schemaVersion.previousVersion,
								current: schemaVersion.currentVersion,
								id: schemaVersion.schemaVersionId
							}
						}
					}
				}
			}
		}
		return root;
	}


	let root = getTree();
	console.log(root);

	function updateTreeWith(updated) {
		root.files[updated.organizationId].files[updated.unitId].files[updated.contextId].files[updated.schemaId].files[updated.schemaVersionId] = {
			type: 'schemaVersion',
			status: updated.status,
			specification: updated.specification,
			description: updated.description,
			previous: updated.previousVersion,
			current: updated.currentVersion,
			id: updated.schemaVersionId
		};
	}

</script>

<svelte:head>
	<title>Home</title>
</svelte:head>

<Card>
	{#if $organizationsStore.length}
		<CardTitle>
			Home
			<span style="margin-left: auto; font-size: 1rem;"><Switch bind:checked={$detailed}>Details</Switch></span>
		</CardTitle>
		<!-- <ValidatedInput type="search" placeholder="Search..."/> -->
		<Folder detailed={$detailed} file={root} first/>
	{/if}

	{#if !$organizationsStore.length}
		<OrganizationAlert/>
	{:else if !$unitsStore.length}
		<UnitAlert/>
	{:else if !$contextsStore.length}
		<ContextAlert/>
	{:else if !$schemasStore.length}
		<SchemaAlert/>
	{:else if !$schemaVersionsStore.length}
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
</Card>

{#if !isEmpty($schemaVersionStore)}
	<VersionContainer on:versionChanged={(v) => updateTreeWith(v.detail)}/>
{/if}