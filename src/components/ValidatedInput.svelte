<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
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

	//this works, but seems wonky
	export let clear = false;
	$: if(clear || !clear) {
		valueValid = false;
		valueInvalid = false;
	}
	
	const valueCheck = (e) => {
		value = e.target.value;

		if(value) {
			valueValid = true;
			valueInvalid = false;
		} else {
			valueValid = false;
			valueInvalid = true;
			validationMessage = "may not be empty";
		}

		if(validator) {
			if(validator(value)) {
				valueValid = true;
				valueInvalid = false;
			} else {
				valueValid = false;
				valueInvalid = true;
				validationMessage = "must be a semantic version number (major.minor.patch, e.g. 1.6.12)";
			}
		}
	}
</script>

<div class="flex-child">
<FormGroup>
	<Label for={id}>{label}</Label>
	<!-- keyup: instant check on input, blur: checks on doing nothing, change: checks on selects -->
	<Input type={type} name={id} id={id} placeholder={label} bind:value={value} disabled={disabled}
	valid={valueValid} invalid={valueInvalid} on:blur={valueCheck} on:keyup={valueCheck} on:change={valueCheck}>
		{#if options}
		<option/>
		{#each options as option}
			<option>{option}</option>
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
		flex-grow: 1;
		margin: 0 1vw;
	}
</style>