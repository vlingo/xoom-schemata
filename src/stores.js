import { writable } from 'svelte/store';

export const firstPage = writable(true);

export const organizationStore = writable(
	{
        organizationId: '',
        name: '',
        description: ''
    }
);
export const organizationsStore = writable([]);

export const unitStore = writable(
	{
        organizationId: '',
        unitId: '',
        name: '',
        description: ''
    }
);
export const unitsStore = writable([]);

export const contextStore = writable(
	{
        
    }
);
export const contextsStore = writable([]);

export const schemaStore = writable(
	{
       
    }
);
export const schemasStore = writable([]);

export const schemaVersionStore = writable(
    {
       
    }
);
export const schemaVersionsStore = writable([]);