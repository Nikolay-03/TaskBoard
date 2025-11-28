<script lang="ts">
	import {
		Command,
		CommandDialog,
		CommandEmpty,
		CommandGroup,
		CommandInput,
		CommandList
	} from '$lib/ui/command';
	import type { Snippet } from 'svelte';

	interface Props {
		open: boolean;
		handleOpenChange: (open: boolean) => void;
		renderSelected?: Snippet;
		renderOptions: Snippet;
		placeholder?: string;
		emptyMessage?: string;
		heading?: string;
	}

	let {
		open,
		handleOpenChange,
		renderSelected,
		renderOptions,
		heading,
		placeholder = 'Search...',
		emptyMessage = 'No results found.'
	}: Props = $props();
</script>

<Command>
	<CommandDialog {open} onOpenChange={handleOpenChange}>
		<CommandInput {placeholder} />
		{@render renderSelected?.()}
		<CommandList>
			<CommandEmpty>{emptyMessage}</CommandEmpty>
			<CommandGroup {heading}>
				{@render renderOptions()}
			</CommandGroup>
		</CommandList>
	</CommandDialog>
</Command>
