import { get } from 'svelte/store';
import { contextStore, organizationStore, unitStore, schemaStore, organizationsStore, unitsStore, contextsStore, schemasStore, schemaVersionStore, schemaVersionsStore } from './stores';


export function getFileString(file, detailed) {
	let fileStrings = [];
	if (file.name) fileStrings[0] = detailed ? `Name: ${file.name}` : file.name;
	if (file.namespace) fileStrings[0] = detailed ? `Namespace: ${file.namespace}` : file.namespace;
	if (file.currentVersion) fileStrings[0] = detailed ? `Version: ${file.currentVersion}` : file.currentVersion;
	if (file.id && detailed) fileStrings[1] = `Id: ${file.id}`;
	//we should consider renaming all id's to 'id': it's easier to have a base element, so you can filter with one condition.
	if (file.organizationId && detailed) fileStrings[1] = `Id: ${file.organizationId}`;
	if (file.unitId && detailed) fileStrings[1] = `Id: ${file.unitId}`;
	if (file.contextId && detailed) fileStrings[1] = `Id: ${file.contextId}`;
	if (file.schemaId && detailed) fileStrings[1] = `Id: ${file.schemaId}`;
	if (file.schemaVersionId && detailed) fileStrings[1] = `Id: ${file.schemaVersionId}`;

	if (file.category && detailed) fileStrings[2] = `Category: ${file.category}`;
	if (file.scope && detailed) fileStrings[3] = `Scope: ${file.scope}`;
	if (file.previousVersion && detailed) fileStrings[4] = `Previous Version: ${file.previousVersion}`;
	if (file.status && detailed) fileStrings[5] = `Status: ${file.status}`;
	return fileStrings.filter(el => el != null && el != '').join(", ");
}

//the deepest path needs be set inside the single stores
export function initStoresOfOne() {
	const deepestLeaf = get(schemaVersionsStore).length > 0 ? { type: "schemaVersion" } :
		get(schemasStore).length > 0 ? { type: "schema" } :
			get(contextsStore).length > 0 ? { type: "context" } :
				get(unitsStore).length > 0 ? { type: "unit" } :
					get(organizationsStore).length > 0 ? { type: "organization" } : "";

	switch (deepestLeaf.type) {
		case "organization": organizationStore.set(get(organizationsStore)[0]);
			break;
		case "unit": unitStore.set(get(unitsStore)[0]);
			break;
		case "context": contextStore.set(get(contextsStore)[0]);
			break;
		case "schema": schemaStore.set(get(schemasStore)[0]);
			break;
		case "schemaVersion": schemaVersionStore.set(get(schemaVersionsStore)[0]);
			break;
	}
	switch (deepestLeaf.type) {
		case "schemaVersion": schemaStore.set(get(schemasStore).find(s => s.schemaId == get(schemaVersionStore).schemaId));
		case "schema": contextStore.set(get(contextsStore).find(c => c.contextId == get(schemaStore).contextId));
		case "context": unitStore.set(get(unitsStore).find(u => u.unitId == get(contextStore).unitId));
		case "unit": organizationStore.set(get(organizationsStore).find(o => o.organizationId == get(unitStore).organizationId));
		case "organization": //do nothing
	}
}


export function isObjectInAStore(file, ...stores) {
	switch (file.type) {
		case "organization":
			return get(organizationStore) ? get(organizationStore).organizationId == file.id : false;
		case "unit":
			return get(unitStore) ? get(unitStore).unitId == file.id : false;
		case "context":
			return get(contextStore) ? get(contextStore).contextId == file.id : false;
		case "schema":
			return get(schemaStore) ? get(schemaStore).schemaId == file.id : false;
		case "schemaVersion":
			return get(schemaVersionStore) ? get(schemaVersionStore).schemaVersionId == file.id : false;
	}
}
// isObjectInAStore is only used once, we could do this inline:
// switch(file.type) {
// 	case "organization": expanded = $organizationStore ? $organizationStore.organizationId === file.id : false;
// 	break;
// 	case "unit": expanded = $unitStore ? $unitStore.unitId === file.id : false;
// 	break;
// 	case "context": expanded = $contextStore ? $contextStore.contextId === file.id : false;
// 	break;
// 	case "schema": expanded = $schemaStore ? $schemaStore.schemaId === file.id : false;
// 	break;
// 	case "schemaVersion":  expanded = $schemaVersionStore ? $schemaVersionStore.schemaVersionId === file.id : false;
// }

export function adjustStoresTo(file) {
	switch (file.type) {
		case "organization":
			setStoreToObjectWithId(organizationStore, organizationsStore, "organizationId", file.id);
			break;
		case "unit":
			setStoreToObjectWithId(unitStore, unitsStore, "unitId", file.id);
			setStoreToObjectWithId(organizationStore, organizationsStore, "organizationId", get(unitStore).organizationId)
			break;
		case "context":
			setStoreToObjectWithId(contextStore, contextsStore, "contextId", file.id);
			setStoreToObjectWithId(unitStore, unitsStore, "unitId", get(contextStore).unitId)
			setStoreToObjectWithId(organizationStore, organizationsStore, "organizationId", get(unitStore).organizationId)
			break;
		case "schema":
			setStoreToObjectWithId(schemaStore, schemasStore, "schemaId", file.id);
			setStoreToObjectWithId(contextStore, contextsStore, "contextId", get(schemaStore).contextId)
			setStoreToObjectWithId(unitStore, unitsStore, "unitId", get(contextStore).unitId)
			setStoreToObjectWithId(organizationStore, organizationsStore, "organizationId", get(unitStore).organizationId)
			break;
		case "schemaVersion":
			setStoreToObjectWithId(schemaVersionStore, schemaVersionsStore, "schemaVersionId", file.id)
			setStoreToObjectWithId(schemaStore, schemasStore, "schemaId", get(schemaVersionStore).schemaId)
			setStoreToObjectWithId(contextStore, contextsStore, "contextId", get(schemaStore).contextId)
			setStoreToObjectWithId(unitStore, unitsStore, "unitId", get(contextStore).unitId)
			setStoreToObjectWithId(organizationStore, organizationsStore, "organizationId", get(unitStore).organizationId)
			break;
	}
	switch (file.type) {
		case "organization":
			resetStore(unitStore);
		case "unit":
			resetStore(contextStore);
		case "context":
			resetStore(schemaStore);
		case "schema":
			resetStore(schemaVersionStore);
		case "schemaVersion":
			break;
	}
}

// yes, the fall-through is meant to be
export function deAdjustStoresTo(type) {
	switch (type) {
		case "organization":
			resetStore(organizationStore);
		case "unit":
			resetStore(unitStore);
		case "context":
			resetStore(contextStore);
		case "schema":
			resetStore(schemaStore);
		case "schemaVersion":
			resetStore(schemaVersionStore);
			break;
	}
}

function resetStore(store) {
	store.set(undefined);
}

function setStoreToObjectWithId(storeOne, storeAll, idProp, id) {
	storeOne.set(get(storeAll).find(obj => obj[idProp] === id))
}

export function isEmpty(obj) {
	for (var _ in obj) return false;
	return true;
}

// yes, the fall-through is meant to be
export function getFullyQualifiedName(type, _) {
	return;
	let fullyQualifiedName = "";
	switch (type) {
		case "schemaVersion": fullyQualifiedName = get(schemaVersionStore).currentVersion + fullyQualifiedName;
		case "schema": fullyQualifiedName = get(schemaStore).name + ":" + fullyQualifiedName;
		case "context": fullyQualifiedName = get(contextStore).namespace + ":" + fullyQualifiedName;
		case "unit": fullyQualifiedName = get(unitStore).name + ":" + fullyQualifiedName;
		case "organization": fullyQualifiedName = get(organizationStore).name + ":" + fullyQualifiedName;
		default: return fullyQualifiedName;
	}
}

//order matters (all elements have two ids + version has ALL ids)
export function idReturner(obj) {
	return obj.schemaVersionId ? obj.schemaVersionId :
		obj.schemaId ? obj.schemaId :
			obj.contextId ? obj.contextId :
				obj.unitId ? obj.unitId :
					obj.organizationId ? obj.organizationId : "";
}
export function stringReturner(obj, detailed) {
	return getFileString(obj, detailed);
}

export function changedSelect(array, detailed) {
	return array.map(obj => {
		return initSelected(obj, stringReturner, idReturner, detailed);
	})
}

function initSelected(obj, stringReturner, idReturner, detailed) {
	if (obj) {
		return {
			id: idReturner(obj),
			text: stringReturner(obj, detailed)
		};
	}
	return {};
}

export const isNameUnique = (name, store, updateStore, key, defineMode) => {
	let count;
	if (defineMode) {
		count = store.filter(org => org.name === name).length;
	} else {
		count = store.filter(org => org.name === name && org[key] !== updateStore[key]).length;
	}
	return count < 1 ? undefined : "Name must be unique";
};
