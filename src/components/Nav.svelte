<script>

import {
    Collapse,
    Navbar,
    NavbarToggler,
    NavbarBrand,
    Nav,
    NavItem,
    NavLink,
    UncontrolledDropdown,
    DropdownToggle,
    DropdownMenu,
    DropdownItem
  } from 'sveltestrap/src';

  let isOpen = false;

  function handleUpdate(event) {
    isOpen = event.detail.isOpen;
  }
</script>

<Navbar style="flex-flow: column wrap;" color="light" light expand="md">
	<NavbarBrand href="/"><span>VLINGO</span>/SCHEMATA</NavbarBrand>
	<NavbarToggler on:click={() => (isOpen = !isOpen)} />
	<Collapse {isOpen} navbar expand="md" on:update={handleUpdate}>
	  <Nav class="ml-auto" navbar>
		<NavItem>
			<NavLink href="">Home</NavLink>
		</NavItem>
		<NavItem>
		  <NavLink href="organization">Organization</NavLink>
		</NavItem>
		<NavItem>
			<NavLink href="unit">Unit</NavLink>
		</NavItem>
		<NavItem>
			<NavLink href="context">Context</NavLink>
		</NavItem>
		<NavItem>
			<NavLink href="schema">Schema</NavLink>
		</NavItem>
		<NavItem>
			<NavLink href="schemaVersion">Schema Version</NavLink>
		</NavItem>
		<!-- <UncontrolledDropdown nav inNavbar>
		  <DropdownToggle nav caret>Options</DropdownToggle>
		  <DropdownMenu right>
			<DropdownItem>Option 1</DropdownItem>
			<DropdownItem>Option 2</DropdownItem>
			<DropdownItem divider />
			<DropdownItem>Reset</DropdownItem>
		  </DropdownMenu>
		</UncontrolledDropdown> -->
	  </Nav>
	</Collapse>
  </Navbar>