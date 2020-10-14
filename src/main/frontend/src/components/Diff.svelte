<script>
	import { mdiArrowRight, mdiCodeBraces, mdiLabelOff, mdiMinusBox, mdiNotEqual, mdiPlusBox, mdiSwapVertical, mdiVariable } from '@mdi/js'
	import Card from "svelte-materialify/src/components/Card";
	import CardActions from "svelte-materialify/src/components/Card/CardActions.svelte";
	import CardText from "svelte-materialify/src/components/Card/CardText.svelte";
	import CardTitle from "svelte-materialify/src/components/Card/CardTitle.svelte";
	import Dialog from "svelte-materialify/src/components/Dialog";
	import Icon from "svelte-materialify/src/components/Icon";
	import List from "svelte-materialify/src/components/List";
	import ListItem from "svelte-materialify/src/components/List/ListItem.svelte";
	import Textarea from "svelte-materialify/src/components/Textarea";

	export let oldSpec;
	export let newSpec;
	export let changes;
	export let showDiffDialog;

	const icons = Object.freeze({
        CHANGE_FIELD: mdiVariable,
        CHANGE_FIELD_VERSION: mdiLabelOff,
        CHANGE_FIELD_TYPE: mdiCodeBraces,
        CHANGE_FIELD_DEFAULT: mdiNotEqual,
        ADD_FIELD: mdiPlusBox,
        CHANGE_TYPE: mdiCodeBraces,
        REMOVE_FIELD: mdiMinusBox,
        MOVE_FIELD: mdiSwapVertical,
        arrowRight: mdiArrowRight,
	});
	const iconFor = (type) => icons[type];
    const colorFor = (type) => {
        switch (type) {
            case "CHANGE_FIELD":
            case "CHANGE_FIELD_VERSION":
            case "CHANGE_FIELD_TYPE":
            case "CHANGE_FIELD_DEFAULT":
            case "MOVE_FIELD":
                return 'warning';
            case "ADD_FIELD":
                return 'primary';
            case "CHANGE_TYPE":
            case "REMOVE_FIELD":
                return 'error';
        }
	}
</script>

<Dialog bind:active={showDiffDialog}>
	<Card>
		<CardTitle>Diff:</CardTitle>
		<CardText>
			<Textarea outlined value={oldSpec} readonly>Old Specification</Textarea>
			<Textarea outlined value={newSpec} readonly>New Specification</Textarea>
			<List disabled dense>
				{#each changes as change}
					<ListItem>
						<Icon class="{colorFor(change.type)}-text" path={iconFor(change.type)}/>
						<span class="subject">{change.subject} </span>
						{#if change.oldValue && change.newValue}
							<span class="old">{change.oldValue}</span>
							<Icon path={icons["arrowRight"]}/>
							<span class="new">{change.newValue}</span>
						{/if}
					</ListItem>
				{/each}
			</List>
		</CardText>
		<CardActions>
			<slot/>
		</CardActions>
	</Card>
</Dialog>

<style>
	.subject {
        font-weight: bold;
    }
    .old {
        text-decoration: line-through;
        color: grey;
    }
</style>