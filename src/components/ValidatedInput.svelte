<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import errors from "../errors";
	export let label = "";
	export let type = "text";
	export let id = label.toLowerCase();
	export let value = "";
	export let disabled = false;
	export let options = [];
	//validator function
	export let validator = null;

	let valueValid = false;
	let valueInvalid = false;
	let validationMessage = "";

	export let clearFlag = false;
	$: if(clearFlag || !clearFlag) {
		valueValid = false;
		valueInvalid = false;
	}

	// ^^^ this is shorter and easier
	// vvv this works if you `bind:clearFlag` and use `clearFlag = false;` in the parent component
	// $: console.log(clearFlag)
	// $: clearFlag = clearValids(clearFlag); //if clearFlag changes, call
	// function clearValids(_) {
	// 	valueValid = false;
	// 	valueInvalid = false;
	// 	return false;
	// }
	
	const valueCheck = (e) => {
		value = e.target.value;

		if(value) {
			valueValid = true;
			valueInvalid = false;
		} else {
			valueValid = false;
			valueInvalid = true;
			validationMessage = errors.EMPTY;
		}

		if(validator) {
			if(validator(value)) {
				valueValid = true;
				valueInvalid = false;
			} else {
				valueValid = false;
				valueInvalid = true;
				validationMessage = errors.VERSION;
			}
		}
	}
</script>

<div class="flex-child">
<FormGroup>
	<Label for={id}>{label}</Label>
	<!-- keyup: instant check on input, blur: checks on doing nothing, change: checks on selects -->
	<Input type={type} name={id} id={id} placeholder={label} bind:value={value} disabled={disabled}
	valid={valueValid} invalid={valueInvalid} on:blur={valueCheck} on:keyup={valueCheck} on:change={valueCheck} on:input={valueCheck}>
		{#if options}
		<option/>
		{#each options as option}
			<option>{option}</option> <!--careful, everything inside counts as the value, newlines/whitespace etc-->
		{/each}
		{/if}
	</Input>

	{#if validationMessage}
		<div class="invalid-feedback">
        	{validationMessage}
    	</div>
	{/if}
</FormGroup>
</div>

<style>
	.flex-child {
		flex: 0 0 50%;
		padding: 12px;
	}
</style>