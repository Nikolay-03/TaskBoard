<script lang="ts">
	import { dndzone } from 'svelte-dnd-action';
	import { CirclePlus } from '@lucide/svelte';
	import { BoardColumn } from '$lib/components/board';
	import { flip } from 'svelte/animate';
	import { type IColumn, useUpdateColumn } from '$api/column';
	import { type ITask, useUpdateTask } from '$api/task';
	import { getRequestErrorMessage } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import { Button } from '$lib/ui/button';
	import { ColumnActionModal } from '$lib/components/columns';

	interface Props {
		columnItems: IColumn[];
		boardId: number;
	}

	let { columnItems = $bindable(), boardId }: Props = $props();

	const updateTaskMutation = useUpdateTask();
	const updateColumnMutation = useUpdateColumn();

	function handleDndConsiderColumns(e: CustomEvent) {
		columnItems = e.detail.items;
	}

	async function handleDndFinalizeColumns(e: CustomEvent) {
		columnItems = e.detail.items;
		const colId = e.detail.info.id;
		const newPosition = columnItems.findIndex((c: IColumn) => c.id === colId) + 1;

		try {
			await updateColumnMutation.mutateAsync({
				id: colId,
				body: {
					position: newPosition
				}
			});
		} catch (err) {
			const errorMessage = getRequestErrorMessage(err);
			toast.error(errorMessage);
		}
	}

	const flipDurationMs = 200;

	function handleDndConsiderCards(cid: number, e: CustomEvent) {
		const colIdx = columnItems.findIndex((c) => c.id === cid);
		columnItems[colIdx].tasks = e.detail.items;
		columnItems = [...columnItems];
	}

	async function handleDndFinalizeCards(cid: number, e: CustomEvent) {
		const { items, info } = e.detail;
		const colIdx = columnItems.findIndex((c) => c.id === cid);
		const taskId = info.id;

		columnItems[colIdx].tasks = items;
		columnItems = [...columnItems];

		const isInThisColumn = items.some((t: ITask) => t.id === taskId);
		if (!isInThisColumn) {
			return;
		}

		const newPosition = items.findIndex((t: ITask) => t.id === taskId) + 1;

		try {
			await updateTaskMutation.mutateAsync({
				id: taskId,
				body: {
					columnId: cid,
					position: newPosition
				}
			});
		} catch (err) {
			const errorMessage = getRequestErrorMessage(err);
			toast.error(errorMessage);
		}
	}
</script>

<section
	class="flex h-fit w-full flex-grow gap-4 overflow-x-auto"
	use:dndzone={{
		items: columnItems,
		flipDurationMs,
		type: 'columns',
		dropTargetClasses: ['!outline-none']
	}}
	on:consider={handleDndConsiderColumns}
	on:finalize={handleDndFinalizeColumns}
>
	{#each columnItems as column (column.id)}
		<div animate:flip={{ duration: flipDurationMs }}>
			<BoardColumn {column} {handleDndConsiderCards} {handleDndFinalizeCards} {flipDurationMs} />
		</div>
	{/each}
	<ColumnActionModal {boardId}>
		{#snippet trigger(props)}
			<Button
				{...props}
				class="flex h-full w-[350px] flex-col gap-3 border-2 border-dashed px-3"
				variant="outline"
			>
				<CirclePlus class="size-15 text-muted-foreground" />
			</Button>
		{/snippet}
	</ColumnActionModal>
</section>
