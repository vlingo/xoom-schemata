import { get } from 'svelte/store';
import { contextStore, organizationStore, unitStore, schemaStore, organizationsStore, unitsStore, contextsStore, schemasStore, schemaVersionStore, schemaVersionsStore } from './stores';

export function selectStringsFrom(arr, stringReturner) {
	return arr.map(obj => {
		return stringReturner(obj);
	})
}

export function initSelected(store, stringReturner) {
	return store? stringReturner((store)) : "";
}

export function orgStringReturner(o) {
	return `${o.name} - ${o.organizationId}`
}

export function unitStringReturner(u) {
	return `${u.name} - ${u.unitId}`
}

export function contextStringReturner(c) {
	return `${c.namespace} - ${c.contextId}`
}

export function schemaStringReturner(s) {
	return `${s.name} - ${s.schemaId}`
}

export function schemaVersionStringReturner(sv) {
	return `${sv.currentVersion} - ${sv.status} - ${sv.schemaVersionId}`
}

 //last index should always be the id!
export function getId(str) {
	const words = str.split(" ");
	return words[words.length-1];
}

//more can be removed
export function getFileString(file) {
	switch(file.type) {
		case "organization": return getOrganizationDetails(file);
			break;
		case "unit": return getUnitDetails(file);
			break;
		case "context": return getContextDetails(file);
			break;
		case "schema": return getSchemaDetails(file);
			break;
		case "schemaVersion": return getSchemaVersionDetails(file);
			break;
		default: console.log("type is non-existent.");
	}
	
}

function filterSchemaAttributes(attributes) {
	return attributes.filter(attrib => attrib !== "previous" && attrib !== "specification" && attrib !== "status");
}
function filterCommonFrom(file) {
	return Object.keys(file).filter(attrib => attrib !== "type" && attrib !== "files" && attrib !== "description");
}
function makeFileString(file, attributes) {
	// return attributes.map(key => `${key}: ${file[key]}`).join(" - ");
	return attributes.map(key => file[key]).join(" - ");
}
function getOrganizationDetails(file) {
	return makeFileString(file, filterCommonFrom(file));
}

function getUnitDetails(file) {
	return makeFileString(file, filterCommonFrom(file));
}
function getContextDetails(file) {
	return makeFileString(file, filterCommonFrom(file));
}
function getSchemaDetails(file) {
	return makeFileString(file, filterCommonFrom(file));
}
function getSchemaVersionDetails(file) {
	return makeFileString(file, filterSchemaAttributes(filterCommonFrom(file)));
}

export function getCompatible(fromElements, predicate, fieldValue) {
	if(!fieldValue) return [];
	return (fromElements).filter(obj => predicate(obj));
}

export function isCompatibleToOrg(obj) {
	return obj.organizationId == get(organizationStore).organizationId;
}
// I think this is already enough, organization is compatible if unit is compatible - it's a tree
// WARNING: If organization1, unit1 can have id "foo" and organization2, unit2 can have id "foo", this needs to be changed!
// e.g. return isCompatibleToOrg(obj) && ...
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
	initStores(array, organizationStore, organizationsStore);
}
export function initUnitStores(array) {
	initStores(array, unitStore, unitsStore, isCompatibleToOrg);
}
export function initContextStores(array) {
	initStores(array, contextStore, contextsStore, isCompatibleToUnit);
}
export function initSchemaStores(array) {
	initStores(array, schemaStore, schemasStore, isCompatibleToContext);
}
export function initSchemaVersionStores(array) {
	initStores(array, schemaVersionStore, schemaVersionsStore, isCompatibleToSchema);
}

// array reset is maybe not needed
// array[0] will be undefined if none exist
function initStores(array, storeOfOne, storeOfAll, predicate) {
	if(array) {
		storeOfAll.set([]);
		storeOfAll.update(arr => arr.concat(...array));
		if(predicate) {
			storeOfOne.set(array.filter(obj => predicate(obj))[0]);
		} else {
			storeOfOne.set(array[0]);
		}
	}
}

export function getRightStore(type) {
	switch(type) {
		case "organization": return {store: organizationStore, stores: organizationsStore};
			break;
		case "unit": return {store: unitStore, stores: unitsStore};
			break;
		case "context": return {store: contextStore, stores: contextsStore};
			break;
		case "schema": return {store: schemaStore, stores: schemasStore};
			break;
		case "schemaVersion": return {store: schemaVersionStore, stores: schemaVersionsStore};
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
			updateOrganizationStoreTo(file);
			break;
		case "unit":
			updateUnitStoreTo(file);
			break;
		case "context":
			updateContextStoreTo(file);
			break;
		case "schema":
			updateSchemaStoreTo(file);
			break;
		case "schemaVersion":
			updateSchemaVersionStoreTo(file);
			break;
		default: console.log("type is non-existent.");
	}
	console.log(get(organizationsStore), get(organizationStore), get(unitsStore), get(unitStore), get(contextsStore), get(contextStore), get(schemasStore), get(schemaStore), get(schemaVersionsStore), get(schemaVersionStore));
}

export function deAdjustStoresTo(file) {
	switch(file.type) {
		case "organization":
			resetStores(organizationStore, unitStore, contextStore, schemaStore, schemaVersionStore);
			break;
		case "unit":
			resetStores(unitStore, contextStore, schemaStore, schemaVersionStore);
			break;
		case "context":
			resetStores(contextStore, schemaStore, schemaVersionStore);
			break;
		case "schema":
			resetStores(schemaStore, schemaVersionStore);
			break;
		case "schemaVersion":
			resetStores(schemaVersionStore);
			break;
		default: console.log("type is non-existent.");
	}
	console.log(get(organizationsStore), get(organizationStore), get(unitsStore), get(unitStore), get(contextsStore), get(contextStore), get(schemasStore), get(schemaStore), get(schemaVersionsStore), get(schemaVersionStore));
}

function resetStores(...stores) {
	stores.forEach(store => store.set({}));
}

function updateOrganizationStoreTo(organizationObj) {
	setOrganizationStoreToOrganizationWithId(organizationObj.id);

	resetStores(unitStore, contextStore, schemaStore, schemaVersionStore);
}
function updateUnitStoreTo(unitObj) {
	setUnitStoreToUnitWithId(unitObj.id);
	setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);

	resetStores(contextStore, schemaStore, schemaVersionStore);
}
function updateContextStoreTo(contextObj) {
	setContextStoreToContextWithId(contextObj.id);
	setUnitStoreToUnitWithId(get(contextStore).unitId);
	setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);

	resetStores(schemaStore, schemaVersionStore);
}
function updateSchemaStoreTo(schemaObj) {
	setSchemaStoreToSchemaWithId(schemaObj.id);
	setContextStoreToContextWithId(get(schemaStore).contextId);
	setUnitStoreToUnitWithId(get(contextStore).unitId);
	setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);

	resetStores(schemaVersionStore);
}

function updateSchemaVersionStoreTo(schemaVersionObj) {
	setSchemaVersionToSchemaVersionWithId(schemaVersionObj.id)
	setSchemaStoreToSchemaWithId(get(schemaVersionStore).schemaId);
	setContextStoreToContextWithId(get(schemaStore).contextId);
	setUnitStoreToUnitWithId(get(contextStore).unitId);
	setOrganizationStoreToOrganizationWithId(get(unitStore).organizationId);
}

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
	// logic if I were to abstract id
	// let id;
	// obj.organizationId? id=obj.organizationId :
	// obj.unitId? id=obj.unitId :
	// obj.contextId? id=obj.contextId :
	// obj.schemaId? id=obj.schemaId :
	// obj.schemaVersionId? id=obj.schemaVersionId :
	// id=null;