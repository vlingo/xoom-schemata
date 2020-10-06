import { get } from 'svelte/store';
import { contextStore, organizationStore, unitStore, schemaStore, organizationsStore, unitsStore, contextsStore, schemasStore, schemaVersionStore, schemaVersionsStore } from './stores';


export function getFileString(file, detailed) {
	switch(file.type) {
		case "organization": return makeStringFrom(file, filterCommonFrom(file, detailed), detailed);
		case "unit": return makeStringFrom(file, filterCommonFrom(file, detailed), detailed);
		case "context": return makeStringFrom(file, filterCommonFrom(file, detailed), detailed);
		case "schema": return makeStringFrom(file, filterSchemaAttributes(filterCommonFrom(file, detailed)), detailed);
		case "schemaVersion": return makeStringFrom(file, filterSchemaVersionAttributes(filterCommonFrom(file, detailed)), detailed);
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
	// console.log(attributes);
	let attributesSorted = [];
	attributes.forEach(attr => {
		if(attr === "name" || attr === "namespace") {
			attributesSorted.unshift(attr);
		} else if(attr === "category") {
			attributesSorted.splice(1, 0, attr);
		} else {
			attributesSorted.push(attr); //works, could also splice every id seperately
		}
	})
	// console.log(attributesSorted);
	if(detailed) return attributesSorted.map(key => {
		if(file[key]){
			return `${key}: ${file[key]}`;
		}
	}).join(" - ");

	return attributesSorted.map(key => {
		if(file[key]){
			return file[key];
		}
	}).join(" - ");
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


export function isObjectInAStore(file) {
	switch(file.type) {
		case "organization":
			return get(organizationStore) ? get(organizationStore).organizationId == file.id : false;
			break;
		case "unit":
			return get(unitStore) ? get(unitStore).unitId == file.id : false;
			break;
		case "context":
			return get(contextStore) ? get(contextStore).contextId == file.id : false;
			break;
		case "schema":
			return get(schemaStore) ? get(schemaStore).schemaId == file.id : false;
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
	stores.forEach(store => store.set(undefined));
}

function setSchemaVersionToSchemaVersionWithId(id) {
	schemaVersionStore.set(
		get(schemaVersionsStore).find(obj => obj.schemaVersionId == id)
	);
}
function setSchemaStoreToSchemaWithId(id) {
	schemaStore.set(
		get(schemasStore).find(obj => obj.schemaId == id)
	);
}
function setContextStoreToContextWithId(id) {
	contextStore.set(
		get(contextsStore).find(obj => obj.contextId == id)
	);
}
function setUnitStoreToUnitWithId(id) {
	unitStore.set(
		get(unitsStore).find(obj => obj.unitId == id)
	);
}
function setOrganizationStoreToOrganizationWithId(id) {
	organizationStore.set(
		get(organizationsStore).find(obj => obj.organizationId == id)
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

//order matters (all elements have two ids + version has ALL ids)
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
	return [{
		id: "",
		text: " "
	}].concat(array.map(obj => {
		return initSelected(obj, stringReturner, idReturner, detailed);
	}))
}

function initSelected(obj, stringReturner, idReturner, detailed) {
	if(obj) {
		return {
			id: idReturner(obj),
			text: stringReturner(obj, detailed)
		};
	}
	return {};
}

// console.log(get(organizationsStore), get(organizationStore), get(unitsStore), get(unitStore), get(contextsStore), get(contextStore), get(schemasStore), get(schemaStore), get(schemaVersionsStore), get(schemaVersionStore));