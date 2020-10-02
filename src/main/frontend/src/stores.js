import { writable } from 'svelte/store';

export const firstPage = writable(true);

export const detailed = writable(false);

// export const organizationStore = writable({});
// export const organizationsStore = writable([]);
export const organizationStore = writable();
export const organizationsStore = writable([]);
export const unitStore = writable();
export const unitsStore = writable([]);
export const contextStore = writable();
export const contextsStore = writable([]);
export const schemaStore = writable();
export const schemasStore = writable([]);
export const schemaVersionStore = writable();
export const schemaVersionsStore = writable([]);