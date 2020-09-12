import { get } from 'svelte/store';
import { organizationStore } from './stores';

export function selectStringsFrom(arr, stringReturner) {
	return arr.map(obj => {
		return stringReturner(obj);
	})
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


// export function getCompatibleUnits(o) {
// 	return ($unitsStore).filter(u => u.organizationId == o.organizationId);
// }


// can use multiple predicates like that
export function isCompatibleToOrg(obj) {
	return obj.organizationId == get(organizationStore).organizationId;
}

export function getCompatible(fromElements, predicate, _) {
	if(!_) return [];
	return (fromElements).filter(obj => predicate(obj));
}