<script lang="ts">
	import { Card, CardHeader, CardTitle, CardContent } from '$lib/ui/card';
	import { flip } from 'svelte/animate';
	import { dndzone } from 'svelte-dnd-action';
	import { BoardCard } from '$lib/components/board';
	import type { IColumn } from '$api/column';
	import { AddTaskButton, CreateTaskModal } from '$lib/components/tasks';

	interface Props {
		column: IColumn;
		handleDndConsiderCards: (id: number, e: CustomEvent) => void;
		handleDndFinalizeCards: (id: number, e: CustomEvent) => void;
		flipDurationMs: number;
	}

	const { column, handleDndFinalizeCards, handleDndConsiderCards, flipDurationMs }: Props =
		$props();
</script>

<Card class="h-full border-2 border-dashed">
	<CardHeader>
		<CardTitle class="text-center">{column.title}</CardTitle>
	</CardHeader>
	<CardContent class="flex h-full w-[350px] flex-col gap-3 px-3">
		<div
			class="flex min-h-5 flex-col gap-3"
			use:dndzone={{ items: column.tasks, flipDurationMs, dropTargetClasses: ['!outline-none'] }}
			on:consider={(e) => handleDndConsiderCards(column.id, e)}
			on:finalize={(e) => handleDndFinalizeCards(column.id, e)}
		>
			{#each column.tasks as item (item.id)}
				<div animate:flip={{ duration: flipDurationMs }}>
					<BoardCard {...item} />
				</div>
			{/each}
			<CreateTaskModal columnId={column.id} boardId={column.boardId}>
				{#snippet trigger(props)}
					<AddTaskButton {flipDurationMs} buttonProps={props} />
				{/snippet}
			</CreateTaskModal>
		</div>
	</CardContent>
</Card>
