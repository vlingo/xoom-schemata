<script>
	import { detailed} from "../stores";
	import { changedSelect, idReturner } from "../utils";
	import ValidatedInput from "./ValidatedInput.svelte";

	export let label = "";
	export let storeOne;
	export let storeAll;
	export let arrayOfSelectables = $storeAll;
	export let containerClasses = "";

	let clearFlag = false;

	$: {changedSelected(selected)};
	function changedSelected(selected) {
		console.log(selected);
		if(selected) $storeOne = ($storeAll).find(obj => idReturner(obj) == selected.id);
	}

	$: {changedSelectables(arrayOfSelectables, $detailed)};
	function changedSelectables(arrayOfSelectables, detailed) {
		select = changedSelect(arrayOfSelectables, detailed);
		selected = select[select.length - 1];
		clearFlag = !clearFlag;
	}
	
	let select;
	let selected;

	//init selected
	let first = true;
	$: if(first && select) {
		selected = select[select.length - 1];
		first = false;
	}

</script>


<ValidatedInput inline {containerClasses} type="select" label={label} bind:value={selected} {clearFlag} options={select}/>