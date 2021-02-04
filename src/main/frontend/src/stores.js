import { writable } from 'svelte/store';

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
				set(window.matchMedia(breakpoints['md-and-down']).matches);
			});
		},
	};
};

export const theme = createLocalStore('theme', 'light')
export const isMobile = isMobileStore();

export const firstPage = writable(true);

export const detailed = writable(false);


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