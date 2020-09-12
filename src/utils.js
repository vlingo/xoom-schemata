import { get } from 'svelte/store';
import { contextStore, organizationStore, unitStore } from './stores';

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


export function getId(str) {
	const words = str.split(" ");
	return words[words.length-1];
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

	// logic if I were to abstract id
	// let id;
	// obj.organizationId? id=obj.organizationId :
	// obj.unitId? id=obj.unitId :
	// obj.contextId? id=obj.contextId :
	// obj.schemaId? id=obj.schemaId :
	// obj.schemaVersionId? id=obj.schemaVersionId :
	// id=null;