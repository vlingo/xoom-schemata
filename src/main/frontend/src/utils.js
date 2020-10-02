import { get } from 'svelte/store';
import { contextStore, organizationStore, unitStore, schemaStore, organizationsStore, unitsStore, contextsStore, schemasStore, schemaVersionStore, schemaVersionsStore } from './stores';

export function selectStringsFrom(arr, stringReturner, idReturner, detailed) {
	return [{
		id: "",
		text: " "
	}].concat(arr.map(obj => {
		return initSelected(obj, stringReturner, idReturner, detailed);
	}))
}

export function orgIdReturner(o) { return o.organizationId }
export function unitIdReturner(u) { return u.unitId }
export function contextIdReturner(c) { return c.contextId }
export function schemaIdReturner(s) { return s.schemaId }
export function versionIdReturner(v) { return v.schemaVersionId }


export function initSelected(obj, stringReturner, idReturner, detailed) {
	if(obj) {
		return {
			id: idReturner(obj),
			text: stringReturner(obj, detailed)
		};
	}
	return {};
}

export function orgStringReturner(o, detailed) {
	return getOrganizationDetails(o, detailed);
}

export function unitStringReturner(u, detailed) {
	return getUnitDetails(u, detailed);
}

export function contextStringReturner(c, detailed) {
	return getContextDetails(c, detailed);
}

export function schemaStringReturner(s, detailed) {
	return getSchemaDetails(s, detailed);
}

export function schemaVersionStringReturner(sv, detailed) {
	return getSchemaVersionDetails(sv, detailed);
}

 //last index should always be the id!
export function getId(str) {
	console.log(str);
	const words = str.split(" ");
	return words[words.length-1];
}

//more can be removed
export function getFileString(file, detailed) {
	switch(file.type) {
		case "organization": return getOrganizationDetails(file, detailed);
			break;
		case "unit": return getUnitDetails(file, detailed);
			break;
		case "context": return getContextDetails(file, detailed);
			break;
		case "schema": return getSchemaDetails(file, detailed);
			break;
		case "schemaVersion": return getSchemaVersionDetails(file, detailed);
			break;
		default: console.log("type is non-existent.");
	}
	
}
function filterSchemaAttributes(attributes) {
	return attributes.filter(attrib => attrib !== "category" && attrib !== "scope");
}
function filterSchemaVersionAttributes(attributes) {
	return attributes.filter(attrib => attrib !== "previous" && attrib !== "specification" && attrib !== "status");
}
function filterCommonFrom(element, detailed) {
	if(detailed) return Object.keys(element).filter(attrib => attrib !== "type" && attrib !== "files" && attrib !== "description");
	return Object.keys(element).filter(attrib => attrib !== "type" && attrib !== "files" && attrib !== "description" && attrib !== "id"
			&& attrib !== "organizationId" && attrib !== "unitId" && attrib !== "contextId" && attrib !== "schemaId" && attrib !== "schemaVersionId");
}
function makeStringFrom(file, attributes, detailed) {
	if(detailed) return attributes.map(key => `${key}: ${file[key]}`).join(" - ");
	return attributes.map(key => file[key]).join(" - ");
}
function getOrganizationDetails(org, detailed) {
	return makeStringFrom(org, filterCommonFrom(org, detailed), detailed);
}

function getUnitDetails(file, detailed) {
	return makeStringFrom(file, filterCommonFrom(file, detailed), detailed);
}
function getContextDetails(file, detailed) {
	return makeStringFrom(file, filterCommonFrom(file, detailed), detailed);
}
function getSchemaDetails(file, detailed) {
	return makeStringFrom(file, filterSchemaAttributes(filterCommonFrom(file, detailed)), detailed);
}
function getSchemaVersionDetails(file, detailed) {
	return makeStringFrom(file, filterSchemaVersionAttributes(filterCommonFrom(file, detailed)), detailed);
}

export function getCompatible(fromElements, predicate, fieldValue) {
	if(!fieldValue || fieldValue === " ") return [];
	return (fromElements).filter(obj => predicate(obj));
}

export function isCompatibleToOrg(obj) {
	return obj.organizationId == get(organizationStore).organizationId;
}
export function isCompatibleToUnit(obj) {
	return obj.unitId == get(unitStore).unitId;
}
export function isCompatibleToContext(obj) {
	return obj.contextId == get(contextStore).contextId;
}
export function isCompatibleToSchema(obj) {
	return obj.schemaId == get(schemaStore).schemaId;
}


export function initOrgStores(array) {
	initStoresOfAll(array, organizationStore, organizationsStore);
}
export function initUnitStores(array) {
	initStoresOfAll(array, unitStore, unitsStore, isCompatibleToOrg);
}
export function initContextStores(array) {
	initStoresOfAll(array, contextStore, contextsStore, isCompatibleToUnit);
}
export function initSchemaStores(array) {
	initStoresOfAll(array, schemaStore, schemasStore, isCompatibleToContext);
}
export function initSchemaVersionStores(array) {
	initStoresOfAll(array, schemaVersionStore, schemaVersionsStore, isCompatibleToSchema);
}


function initStoresOfAll(array, storeOfOne, storeOfAll, predicate) {
	if(array.length > 0) {
		storeOfAll.set([]);
		storeOfAll.update(arr => arr.concat(...array));
	}
}

//the deepest path needs be set inside the single stores
export function initStoresOfOne() {
	const deepestLeaf = get(schemaVersionsStore).length > 0 ? { type: "schemaVersion"} : 
						get(schemasStore).length > 0 ? { type: "schema"} : 
						get(contextsStore).length > 0 ? { type: "context"} : 
						get(unitsStore).length > 0 ? { type: "unit"} : 
						get(organizationsStore).length > 0 ? { type: "organization"} : "";
	
	switch(deepestLeaf.type) {
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
		default: console.log("type is non-existent.");
	}
	switch(deepestLeaf.type) {
		case "schemaVersion": schemaStore.set(get(schemasStore).find(s => s.schemaId == get(schemaVersionStore).schemaId));
		case "schema": contextStore.set(get(contextsStore).find(c => c.contextId == get(schemaStore).contextId));
		case "context": unitStore.set(get(unitsStore).find(u => u.unitId == get(contextStore).unitId));
		case "unit": organizationStore.set(get(organizationsStore).find(o => o.organizationId == get(unitStore).organizationId));
		case "organization": //do nothing
		break;
		default: console.log("type is non-existent.");
	}
}

//order matters
export function returnId(obj) {
	if(obj.schemaVersionId) return obj.schemaVersionId;
	if(obj.schemaId) return obj.schemaId;
	if(obj.contextId) return obj.contextId;
	if(obj.unitId) return obj.unitId;
	if(obj.organizationId) return obj.organizationId;
}

export function isObjectInAStore(file) {
	switch(file.type) {
		case "organization":
			return get(organizationStore) ? get(organizationStore).organizationId == file.id : false;
			break;
		case "unit":
			return get(unitStore).unitId ? get(unitStore).unitId == file.id : false;
			break;
		case "context":
			return get(contextStore).contextId ? get(contextStore).contextId == file.id : false;
			break;
		case "schema":
			return get(schemaStore).schemaId ? get(schemaStore).schemaId == file.id : false;
			break;
		case "schemaVersion":
			return get(schemaVersionStore) ? get(schemaVersionStore).schemaVersionId == file.id : false;
			break;
		default: console.log("type root");
	}
}

export function adjustStoresTo(file) {
	switch(file.type) {
		case "organization":
			setOrganizationStoreToOrganizationWithId(file.id);
			break;
		case "unit":
			setUnitStoreToUnitWithId(file.id);
			setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);
			break;
		case "context":
			setContextStoreToContextWithId(file.id);
			setUnitStoreToUnitWithId(get(contextStore).unitId);
			setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);
			break;
		case "schema":
			setSchemaStoreToSchemaWithId(file.id);
			setContextStoreToContextWithId(get(schemaStore).contextId);
			setUnitStoreToUnitWithId(get(contextStore).unitId);
			setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);
			break;
		case "schemaVersion":
			setSchemaVersionToSchemaVersionWithId(file.id)
			setSchemaStoreToSchemaWithId(get(schemaVersionStore).schemaId);
			setContextStoreToContextWithId(get(schemaStore).contextId);
			setUnitStoreToUnitWithId(get(contextStore).unitId);
			setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);
			break;
		default: console.log("type is non-existent.");
	}
	switch(file.type) {
		case "organization":
			resetStores(unitStore);
		case "unit":
			resetStores(contextStore);
		case "context":
			resetStores(schemaStore);
		case "schema":
			resetStores(schemaVersionStore);
		case "schemaVersion":
			break;
		default: console.log("type is non-existent.");
	}
}

// console.log(get(organizationsStore), get(organizationStore), get(unitsStore), get(unitStore), get(contextsStore), get(contextStore), get(schemasStore), get(schemaStore), get(schemaVersionsStore), get(schemaVersionStore));

// yes, the fall-through is meant to be
export function deAdjustStoresTo(type) {
	switch(type) {
		case "organization":
			resetStores(organizationStore);
		case "unit":
			resetStores(unitStore);
		case "context":
			resetStores(contextStore);
		case "schema":
			resetStores(schemaStore);
		case "schemaVersion":
			resetStores(schemaVersionStore);
			break;
		default: console.log("type is non-existent.");
	}
}

function resetStores(...stores) {
	stores.forEach(store => store.set({}));
}
// ummm just use find..
function setSchemaVersionToSchemaVersionWithId(id) {
	schemaVersionStore.set(
		get(schemaVersionsStore).filter(obj => obj.schemaVersionId == id)[0]
	);
}
function setSchemaStoreToSchemaWithId(id) {
	schemaStore.set(
		get(schemasStore).filter(obj => obj.schemaId == id)[0]
	);
}
function setContextStoreToContextWithId(id) {
	contextStore.set(
		get(contextsStore).filter(obj => obj.contextId == id)[0]
	);
}
function setUnitStoreToUnitWithId(id) {
	unitStore.set(
		get(unitsStore).filter(obj => obj.unitId == id)[0]
	);
}
function setOrganizationStoreToOrganizationWithId(id) {
	organizationStore.set(
		get(organizationsStore).filter(obj => obj.organizationId == id)[0]
	);
}

export function isStoreEmpty(store) {
	for(var _ in store) return false;
	return true;
}

// yes, the fall-through is meant to be
export function getFullyQualifiedName(type, _) {
	return;
	let fullyQualifiedName = "";
	switch(type) {
		case "schemaVersion": fullyQualifiedName = get(schemaVersionStore).currentVersion + fullyQualifiedName;
		case "schema": fullyQualifiedName = get(schemaStore).name + ":" + fullyQualifiedName;
		case "context": fullyQualifiedName = get(contextStore).namespace + ":" + fullyQualifiedName;
		case "unit": fullyQualifiedName = get(unitStore).name + ":" + fullyQualifiedName;
		case "organization": fullyQualifiedName = get(organizationStore).name + ":" + fullyQualifiedName;
		default: return fullyQualifiedName;
	}
}

//order matters, all elements have two ids + version has ALL ids
export function idReturner(obj) {
	let id;
	obj.schemaVersionId? id = obj.schemaVersionId :
	obj.schemaId? id = obj.schemaId :
	obj.contextId? id = obj.contextId :
	obj.unitId? id = obj.unitId :
	obj.organizationId? id = obj.organizationId :
	id = "";
	return id;
}
export function stringReturner(obj, detailed) {
	return makeStringFrom(obj, filterCommonFrom(obj, detailed), detailed);
}

export function changedSelect(array, detailed) {
	return selectStringsFrom(array, stringReturner, idReturner, detailed);
}