<script lang="ts">
	import { dndzone } from 'svelte-dnd-action';
	import { BoardColumn } from '$lib/components/board';
	import { flip } from 'svelte/animate';
	import type { IColumn } from '$api/column';
	import {useUpdateTask} from "$api/task";
	import {getRequestErrorMessage} from "$lib/utils";
	import {toast} from "svelte-sonner";

	interface Props {
		columnItems: IColumn[];
	}

	let { columnItems= $bindable() }: Props = $props();
	const updateTaskMutation = useUpdateTask();
	function handleDndConsiderColumns(e: CustomEvent) {
		columnItems = e.detail.items;
	}

	function handleDndFinalizeColumns(e: CustomEvent) {
		columnItems = e.detail.items;
	}

	const flipDurationMs = 200;

	function handleDndConsiderCards(cid: number, e: CustomEvent) {
		const colIdx = columnItems.findIndex((c) => c.id === cid);
		columnItems[colIdx].tasks = e.detail.items;
		columnItems = [...columnItems];
	}

	const handleDndFinalizeCards = async (cid: number, e: CustomEvent) => {
		const { items, info } = e.detail;
		const colIdx = columnItems.findIndex((c) => c.id === cid);
		const taskId = info.id;

		columnItems[colIdx].tasks = items;
		columnItems = [...columnItems];

		const isInThisColumn = items.some((t) => t.id === taskId);
		if (!isInThisColumn) {
			return;
		}

		const newPosition = items.findIndex((t) => t.id === taskId) + 1;

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
	};

</script>

<section
	class="flex h-fit w-full flex-grow gap-4 overflow-x-auto"
	use:dndzone={{
		items: columnItems,
		flipDurationMs,
		type: 'columns',
	}}
	on:consider={handleDndConsiderColumns}
	on:finalize={handleDndFinalizeColumns}
>
	{#each columnItems as column (column.id)}
		<div animate:flip={{ duration: flipDurationMs }}>
			<BoardColumn {column} {handleDndConsiderCards} {handleDndFinalizeCards} {flipDurationMs} />
		</div>
	{/each}
</section>
