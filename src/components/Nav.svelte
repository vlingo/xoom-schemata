<script>
	import { mdiEllipseOutline, mdiFactory, mdiFileDocument, mdiHome, mdiStore, mdiTag } from '@mdi/js';
	import { Collapse, Navbar, NavbarToggler, NavbarBrand, Nav, NavItem, NavLink } from 'sveltestrap/src';
	import Icon from './Icon.svelte';
	import { stores } from '@sapper/app';
	import { contextsStore, organizationsStore, schemasStore, unitsStore } from '../stores';
	const { page } = stores();
	$: ({ path } = $page);

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
			<NavItem active={path === "/" || ""} class="d-flex align-items-center mx-3">
				<Icon icon={mdiHome}/>
				<NavLink href="">Home</NavLink>
			</NavItem>
			<NavItem active={path === "/organization"} class="d-flex align-items-center mx-3">
				<Icon icon={mdiFactory}/>
				<NavLink href="organization">Organization</NavLink>
			</NavItem>
			{#if $organizationsStore.length > 0}
				<NavItem active={path === "/unit"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiStore}/>
					<NavLink href="unit">Unit</NavLink>
				</NavItem>
			{#if $unitsStore.length > 0}
				<NavItem active={path === "/context"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiEllipseOutline}/>
					<NavLink href="context">Context</NavLink>
				</NavItem>
			{#if $contextsStore.length > 0}
				<NavItem active={path === "/schema"} class="d-flex align-items-center mx-3">
					<Icon icon={mdiFileDocument}/>
					<NavLink href="schema">Schema</NavLink>
				</NavItem>
			{#if $schemasStore.length > 0}
				<NavItem active={path === "/schemaVersion"} class="d-flex align-items-center mx-3">
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