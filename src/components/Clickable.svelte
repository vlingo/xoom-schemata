<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore} from "../utils";
	import { contextStore, organizationStore, schemaStore, schemaVersionStore, unitStore } from '../stores';

	export let file;

	// $: if((item == $currentOrg) || (item == $currentUnit) || (item == $currentContext) || (item == $currentSchema) || (item == $currentVersion)) setCurrent();


	// $: active = (item == $currentOrg) || (item == $currentUnit) || (item == $currentContext) || (item == $currentSchema) || (item == $currentVersion);
	// maybe active needs to have influence on the stores, so maybe $: if(selected) {adjustStoresTo(file) setCurrent()} else {deAdjustStoresTo(file)} 
	// $: selected = isObjectInAStore(file) && active;

	let selected = false;
	$: unSelected = !selected;
	let item;

	$: if(($organizationStore || $unitStore || $contextStore || $schemaStore || $schemaVersionStore) && (isObjectInAStore(file))) {
		selected = true;
	} else {
		selected = false;
	}

	function chooseThis() {
		if(!selected) {
			adjustStoresTo(file);
			// selected = true;
			// setCurrent();
		} else {
			deAdjustStoresTo(file);
			// selected = false;

		}
		// store.set(get(stores).filter(obj => returnId(obj) == file.id)[0]);
	}

	// $: active = (item == $current);
</script>

<div class:selected={selected} class:unSelected={unSelected} on:click={chooseThis}>
	<slot/>
</div>


<style>
	.selected {
		font-weight: bold;
		/* background-color: var(--vlingo-color1);  only do this on the schemaVersion*/
	}
	.unSelected {
		font-weight: lighter;
	}
</style>