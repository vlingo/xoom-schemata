<script>
	import { detailed} from "../../stores";
	import { changedSelect, idReturner } from "../../utils";
	import ValidatedInput from "./ValidatedInput.svelte";

	export let label = "";
	export let storeOne;
	export let storeAll;
	export let arrayOfSelectables;
	export let containerClasses = "";

	let clearFlag = false;

	$: {changedSelected(selected)};
	function changedSelected(selected) {
		console.log(selected);
		$storeOne = selected ? ($storeAll).find(obj => idReturner(obj) == selected[0]) : undefined;
	}

	$: {changedSelectables(arrayOfSelectables, $detailed)};
	function changedSelectables(arrayOfSelectables, detailed) {
		select = changedSelect(arrayOfSelectables, detailed);
		selected = [select[select.length - 1]];
		clearFlag = !clearFlag;
	}
	
	let select;
	let selected;

	//init selected
	let first = true;
	$: if(first && select) {
		selected = [select[select.length - 1]];
		first = false;
	}
</script>


<ValidatedInput {storeAll} inline {containerClasses} type="select" label={label} bind:value={selected} {clearFlag} options={select}/>