<script>
	import { mdiEllipseOutline, mdiFactory, mdiFileDocument, mdiHome, mdiStore, mdiTag } from '@mdi/js';
	import { Collapse, Navbar, NavbarToggler, NavbarBrand, Nav, NavItem, NavLink } from 'sveltestrap/src';
	import Icon from './Icon.svelte';
	import { contextsStore, organizationsStore, schemasStore, unitsStore } from '../stores';
	
	export let segment;

	let isOpen = false;

	function handleUpdate(event) {
		isOpen = event.detail.isOpen;
	}
</script>

<Navbar style="flex-flow: column wrap;" color="light" light expand="md">
	<NavbarBrand href="."><span>VLINGO</span>/SCHEMATA</NavbarBrand>
	<NavbarToggler on:click={() => (isOpen = !isOpen)} />
	<Collapse {isOpen} navbar expand="md" on:update={handleUpdate}>
		<Nav class="ml-auto" navbar>
			<NavItem active={!segment} class="d-flex align-items-center mx-3 border-right border-primary pr-4">
				<Icon icon={mdiHome}/>
				<NavLink href="">Home</NavLink>
			</NavItem>
			<NavItem active={segment === "organization"} class="d-flex align-items-center mx-3">
				<Icon icon={mdiFactory}/>
				<NavLink href="organization">Organization</NavLink>
			</NavItem>
			{#if $organizationsStore.length > 0}
				<NavItem active={segment === "unit"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiStore}/>
					<NavLink href="unit">Unit</NavLink>
				</NavItem>
			{#if $unitsStore.length > 0}
				<NavItem active={segment === "context"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiEllipseOutline}/>
					<NavLink href="context">Context</NavLink>
				</NavItem>
			{#if $contextsStore.length > 0}
				<NavItem active={segment === "schema"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiFileDocument}/>
					<NavLink href="schema">Schema</NavLink>
				</NavItem>
			{#if $schemasStore.length > 0}
				<NavItem active={segment === "schemaVersion"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiTag}/>
					<NavLink href="schemaVersion">Schema Version</NavLink>
				</NavItem>
			{/if}
			{/if}
			{/if}
			{/if}
		<!-- maybe themes..
			<UncontrolledDropdown nav inNavbar>
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