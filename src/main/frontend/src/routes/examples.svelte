<script>
	import Button from 'sveltestrap/src/Button.svelte';
	import Col from 'sveltestrap/src/Col.svelte';
	import Row from 'sveltestrap/src/Row.svelte';
	import { Card, CardBody, Form, FormGroup, FormText, Input, Label, CustomInput } from 'sveltestrap/src';
	
	

  let selectText = "";
  let selectInvalid;
  let selectValid;

  const checkSelect = (e) => {
	selectText = e.target.value;
	console.log(selectText);
	if(!selectText) {
		  selectInvalid = true;
		  selectValid = false;
	  	} else {
			  selectInvalid = false;
			  selectValid = true;
		  }
	}

	const clear = () => {
		selectText = "";
		selectInvalid = false;
		selectValid = false;
	}

	const submit = () => {console.log("RTTR")}
</script>




<svelte:head>
	<title>Browse</title>
</svelte:head>








<Card>
	<CardBody>
		Hello World
		<Row>
			<Col>
				<Button color="primary" outline>Hello World!</Button>
			</Col>
		</Row>
		Form:
		  <Form on:submit={submit}>
			<FormGroup>
			  <Label for="exampleEmail">Plain Text (Static)</Label>
			  <Input plaintext value="Some plain text/ static value" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleEmail">Email</Label>
			  <Input
				type="email"
				name="email"
				id="exampleEmail"
				placeholder="with a placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="examplePassword">Password</Label>
			  <Input
				type="password"
				name="password"
				id="examplePassword"
				placeholder="password placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleUrl">Url</Label>
			  <Input
				type="url"
				name="url"
				id="exampleUrl"
				placeholder="url placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleNumber">Number</Label>
			  <Input
				type="number"
				name="number"
				id="exampleNumber"
				placeholder="number placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleDatetime">Datetime</Label>
			  <Input
				type="datetime"
				name="datetime"
				id="exampleDatetime"
				placeholder="datetime placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleDate">Date</Label>
			  <Input
				type="date"
				name="date"
				id="exampleDate"
				placeholder="date placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleTime">Time</Label>
			  <Input
				type="time"
				name="time"
				id="exampleTime"
				placeholder="time placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleColor">Color</Label>
			  <Input
				type="color"
				name="color"
				id="exampleColor"
				placeholder="color placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleSearch">Search</Label>
			  <Input
				type="search"
				name="search"
				id="exampleSearch"
				placeholder="search placeholder" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleSelect">Select</Label>
			  <Input type="select" name="select" id="exampleSelect">
				<option>1</option>
				<option>2</option>
				<option>3</option>
				<option>4</option>
				<option>5</option>
			  </Input>
			</FormGroup>
			<!-- <FormGroup>
			  <Label for="exampleSelectMulti">Select Multiple</Label>
			  <Input type="select" name="selectMulti" id="exampleSelectMulti" multiple>
				<option>1</option>
				<option>2</option>
				<option>3</option>
				<option>4</option>
				<option>5</option>
			  </Input>
			</FormGroup> -->
			<FormGroup>
			  <Label for="exampleText">Text Area</Label>
			  <Input type="textarea" name="text" id="exampleText" />
			</FormGroup>
			<FormGroup>
			  <Label for="exampleFile">File</Label>
			  <Input type="file" name="file" id="exampleFile" />
			  <FormText color="muted">
				This is some placeholder block-level help text for the above input. It's a
				bit lighter and easily wraps to a new line.
			  </FormText>
			</FormGroup>
			<FormGroup check>
			  <Label check>
				<Input type="radio" />
				Option one 
			  </Label></FormGroup>
			  <FormGroup check>
			  <Label check>
				<Input type="radio" />
				Option two 
			  </Label>
			</FormGroup>
			<FormGroup check>
			  <Label check>
				<Input type="checkbox" />
				Check me out
			  </Label>
			</FormGroup>

			^Doesn't work this way, but like this:

			<FormGroup>
				<Label for="exampleCheckbox">Checkboxes</Label>
				<div>
				  <CustomInput
					type="checkbox"
					id="exampleCustomCheckbox"
					label="Check this custom checkbox" />
				  <CustomInput
					type="checkbox"
					id="exampleCustomCheckbox2"
					label="Or this one" />
				  <CustomInput
					type="checkbox"
					id="exampleCustomCheckbox3"
					label="But not this disabled one"
					disabled />
				  <CustomInput
					type="checkbox"
					id="exampleCustomCheckbox4"
					label="Can't click this label to check!"
					htmlFor="exampleCustomCheckbox4_X"
					disabled />
				</div>
			  </FormGroup>
			  <FormGroup>
				<Label for="exampleCheckbox">Radios</Label>
				<div>
				  <CustomInput
					type="radio"
					id="exampleCustomRadio"
					name="customRadio"
					label="Select this custom radio" />
				  <CustomInput
					type="radio"
					id="exampleCustomRadio2"
					name="customRadio"
					label="Or this one" />
				  <CustomInput
					type="radio"
					id="exampleCustomRadio3"
					label="But not this disabled one"
					disabled />
				  <CustomInput
					type="radio"
					id="exampleCustomRadio4"
					label="Can't click this label to select!"
					htmlFor="exampleCustomRadio4_X"
					disabled />
				</div>
			  </FormGroup>
			  <FormGroup>
				<Label for="exampleCheckbox">Switches</Label>
				<div>
				  <CustomInput
					type="switch"
					id="exampleCustomSwitch"
					name="customSwitch"
					label="Turn on this custom switch" />
				  <CustomInput
					type="switch"
					id="exampleCustomSwitch2"
					name="customSwitch"
					label="Or this one" />
				  <CustomInput
					type="switch"
					id="exampleCustomSwitch3"
					label="But not this disabled one"
					disabled />
				  <CustomInput
					type="switch"
					id="exampleCustomSwitch4"
					label="Can't click this label to turn on!"
					htmlFor="exampleCustomSwitch4_X"
					disabled />


					<Label>Pick an option - {selectText}</Label>
					<button on:click|preventDefault={clear}>clear prevent default button</button>
					<!-- on:blur={checkSelect} -->
					<Button color="primary" outline on:click={clear}>clear</Button>
					
					<CustomInput
					bind:value={selectText}
					type="select"
					invalid={selectInvalid}
					valid={selectValid}
					on:change={checkSelect}
					>
						<option />
						<option value={1}>Uno</option>
						<option value={2}>Dos</option>
						<option value={3}>Tres</option>
						<option value={4}>Quatro</option>
				</CustomInput>
				</div>
			  </FormGroup>
		  </Form>

		  <Button color="primary" outline on:click={clear}>clear</Button>

	</CardBody>
</Card>
  


<style>
	
</style>