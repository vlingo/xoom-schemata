import {get, writable} from 'svelte/store';

export function createLocalStore(key, initialValue) {
	const localValue = process.browser ? localStorage.getItem(key) : initialValue;
	const { subscribe, set } = writable(localValue);

	return {
		subscribe,
		set: (value) => {
			if (process.browser) {
				localStorage.setItem(key, value);
			}
			set(value)
		},
	};
}

export function isMobileStore() {
	const { subscribe, set } = writable(false);
	return {
		subscribe,
		set,
		check: () => {
			import('svelte-materialify/src/utils/breakpoints').then(({ default: breakpoints }) => {
				// Firefox responsive iframe issue : Temporary fix
				if(breakpoints['md-and-down'] ===  "only screen and (max-width: NaNpx)")
					set(true)
				else
				  set(window.matchMedia(breakpoints['md-and-down']).matches);
			});
		},
	};
}

export const theme = createLocalStore('theme', 'light')
export const isMobile = isMobileStore();

export const firstPage = writable(true);

export const detailed = writable(false);

function schemataStore(value, key) {
	const { subscribe, set } = writable(value);
	return {
		subscribe,
		set: value => {
			set(value);
			let data = {
				organizationsStore: key === 'organizations' ? value : get(organizationsStore),
				unitsStore: key === 'units' ? value : get(unitsStore),
				contextsStore: key === 'contexts' ? value : get(contextsStore),
				schemasStore: key === 'schemas' ? value : get(schemasStore),
				schemaVersionsStore: key === 'schemaVersions' ? value : get(schemaVersionsStore),
			};
			if (process.browser) window.parent.postMessage(JSON.stringify(data), "*")
		},
	};
}

export const organizationStore = writable();
export const organizationsStore = schemataStore([], 'organizations');
export const unitStore = writable();
export const unitsStore = schemataStore([], 'units');
export const contextStore = writable();
export const contextsStore = schemataStore([], 'contexts');
export const schemaStore = writable();
export const schemasStore = schemataStore([], 'schemas');
export const schemaVersionStore = writable();
export const schemaVersionsStore = schemataStore([], 'schemaVersions');

//maybe adding the compatibles right here would be better, also we could have less code duplication

// this could potentially make things easier or worse.
// function createOrganizationStore() {
// 	const { subscribe, set, update } = writable();
// 	return { subscribe, set, update,
// 		resetOthers: () => {
// 			unitStore.set(undefined);
// 			unitStore.resetOthers();
// 		}
// 	};
// }
// function createUnitStore() {
// 	const { subscribe, set, update } = writable();
// 	return { subscribe, set, update,
// 		resetOthers: () => {
// 			contextStore.set(undefined);
// 			contextStore.resetOthers();
// 		}
// 	};
// }
// function createContextStore() {
// 	const { subscribe, set, update } = writable();
// 	return { subscribe, set, update,
// 		resetOthers: () => {
// 			schemaStore.set(undefined);
// 			schemaStore.resetOthers();
// 		}
// 	};
// }
// function createSchemaStore() {
// 	const { subscribe, set, update } = writable();
// 	return { subscribe, set, update,
// 		resetOthers: () => {
// 			schemaVersionStore.set(undefined);
// 		}
// 	};
// }

// export const organizationStore = createOrganizationStore();
// export const unitStore = createUnitStore();
// export const contextStore = createContextStore();
// export const schemaStore = createSchemaStore();
