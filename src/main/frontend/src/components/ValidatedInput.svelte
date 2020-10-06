<script>
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput, Button } from 'sveltestrap/src';
	import errors from "../errors";
	export let label = "";
	export let placeholder = label;
	export let type = "text";
	export let id = label.toLowerCase();
	export let value = "";
	export let disabled = false;
	export let readonly = false;
	export let options = "";
	//validator function
	export let validator = null;
	export let invalidString = "";
	export let rows = "";
	export let containerClasses = "flex-child";
	export let inline = false;

	let formGroupClasses = inline? "form-inline" : "";
	let labelClasses = inline? "justify-content-start" : "";
	let inputContainerClasses = inline? "align-self-end w-75" : "";
	let inputClasses = inline? "w-75" : "";

	let valueValid = false;
	let valueInvalid = false;
	let validationMessage = "";

	export let clearFlag = false;
	$: if(clearFlag || !clearFlag) {
		valueValid = false;
		valueInvalid = false;
	}
	// $: if(value || !value) { valueCheck } ?
	
	$: if(options || !options) {
		// console.log("test", label);
	}
	
	const valueCheck = (e) => {
		//if select
		if(options && e.type !== "input") {
			// console.log(e.type, e.target.selectedOptions[0].__value);
			value = e.target.selectedOptions[0].__value;
			if(value.id) {
				valueValid = true;
				valueInvalid = false;
			} else {
				valueValid = false;
				valueInvalid = true;
				validationMessage = errors.EMPTY;
			return;
		}
		}
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
				validationMessage = invalidString;
			}
		}
	}
</script>

<div class={containerClasses}>
<FormGroup class={formGroupClasses}>
	<div class:label-container={inline}>
		<Label class={labelClasses} for={id}>{label}</Label>
	</div>
	<!-- keyup: instant check on input, blur: checks on doing nothing, change: checks on selects -->
	<div class={inputContainerClasses}>
		<Input class={inputClasses} type={type} name={id} id={id} placeholder={placeholder} bind:value={value} disabled={disabled}
		valid={valueValid} invalid={valueInvalid} on:blur={valueCheck} on:keyup={valueCheck} on:change={valueCheck} on:input={valueCheck} {rows} {readonly}>
			{#if options}
				<!-- <option/> -->
				{#each options as option}
					{#if option.text}
						<option selected={option.id === value.id} value={option}>{option.text}</option> <!--careful, everything inside counts as the value, newlines/whitespace etc-->
					{:else}
						<option>{option}</option>
					{/if}
				{/each}
				<!-- {#if options}
						<option/>
						{#each options as option}
							<option>{option}</option> 
						{/each}
					{/if} -->
			{/if}
		</Input>

		{#if validationMessage}
			<div class="invalid-feedback">
        		{validationMessage}
    		</div>
	{/if}
	</div>
</FormGroup>
</div>

<style>
	.label-container {
		width: 10rem;
		padding: 0 12px;
	}
	.flex-child {
		flex: 0 0 50%;
		padding: 12px;
	}
</style>