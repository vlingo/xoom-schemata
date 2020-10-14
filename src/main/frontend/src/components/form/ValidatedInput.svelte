<script>
	// import { FormGroup, Input, Label } from 'sveltestrap/src';
	import { Row, Col, TextField, Textarea, Select } from 'svelte-materialify/src';
import ListItem from 'svelte-materialify/src/components/List/ListItem.svelte';
	import errors from "../../errors";
import { detailed, organizationsStore } from '../../stores';
import { idReturner, stringReturner } from '../../utils';
	export let label = "";
	export let placeholder = undefined;
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
	export let outlined = false;
	export let storeAll;

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
	
	const valueCheck = (e) => {
		// console.log(value, e.type);
		//if select
		if(options && e.type !== "input") {
			// console.log(e.type, e.target.selectedOptions[0].__value);
			value = e.target.selectedOptions[0].__value;
			if(value.text) {
				if(value.id) {
					valueValid = true;
					valueInvalid = false;
				} else {
					valueValid = false;
					valueInvalid = true;
					validationMessage = errors.EMPTY;
				}
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
	const getTextFromId = (val) => typeof val[0] === 'string' ? stringReturner($storeAll.find(element => idReturner(element) == val[0]), $detailed) : "";
	const notEmpty = (value) => !!value ? undefined : "may not be empty";
	const rules = validator ? [notEmpty, validator] : [notEmpty];
</script>
<!-- keyup: instant check on input, blur: checks on doing nothing, change: checks on selects -->
<!--careful, everything inside <option></option> counts as the value, newlines/whitespace etc-->
{#if type === "text"}
	<TextField {outlined} {placeholder} bind:value={value} {disabled} {rules} validateOnBlur {readonly}>{label}</TextField>
{:else if type === "textarea"}
	<Textarea {outlined} {placeholder} bind:value={value} {disabled} {rules} validateOnBlur {readonly}>{label}</Textarea>
{:else if type === "select"}
	{#if $storeAll}
		<div class={containerClasses}>
			<Select items={options} bind:value={value} mandatory callback={getTextFromId}>
				<span let:item slot="item">
					<ListItem value={item.id}>
						{item.text}
					</ListItem>
				</span>
				{label}
			</Select>
		</div>
	{:else}
		<Select items={options} bind:value={value} mandatory>{label}</Select>
	{/if}
{/if}

<!-- <div class={containerClasses}>
<FormGroup class={formGroupClasses}>
	<div class:label-container={inline}>
		<Label class={labelClasses} for={id}>{label}</Label>
	</div>
	
	<div class={inputContainerClasses}>
		<Input class={inputClasses} type={type} name={id} id={id} placeholder={placeholder} bind:value={value} disabled={disabled}
		valid={valueValid} invalid={valueInvalid} on:blur={valueCheck} on:keyup={valueCheck} on:change={valueCheck} on:input={valueCheck} {rows} {readonly}>
			{#if options}
				{#each options as option}
					{#if option.text}
						<option selected={option.id === value.id} value={option}>{option.text}</option>
					{:else}
						<option>{option}</option>
					{/if}
				{/each}
			{/if}
		</Input>

		{#if validationMessage}
			<div class="invalid-feedback">
        		{validationMessage}
    		</div>
	{/if}
	</div>
</FormGroup>
</div> -->

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