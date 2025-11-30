<script lang="ts">
	import { Card, CardHeader, CardTitle, CardContent } from '$lib/ui/card';
	import { flip } from 'svelte/animate';
	import { dndzone } from 'svelte-dnd-action';
	import { type IColumn } from '$api/column';
	import {AddTaskButton, TaskActionModal, TaskCard} from '$lib/components/tasks';
	import { Button } from '$lib/ui/button';
	import { CogIcon } from '@lucide/svelte';
	import { ColumnActionModal } from '$lib/components/columns';

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
		<div class="flex justify-between gap-2">
			<CardTitle>{column.title}</CardTitle>
			<ColumnActionModal boardId={column.boardId} mode="edit" defaultValues={{ ...column }}>
				{#snippet trigger(props)}
					<Button {...props} variant="clean" size="fit">
						<CogIcon class="text-muted-foreground" />
					</Button>
				{/snippet}
			</ColumnActionModal>
		</div>
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
					<TaskCard {...item} />
				</div>
			{/each}
			<TaskActionModal columnId={column.id} boardId={column.boardId}>
				{#snippet trigger(props)}
					<AddTaskButton {flipDurationMs} buttonProps={props} />
				{/snippet}
			</TaskActionModal>
		</div>
	</CardContent>
</Card>
