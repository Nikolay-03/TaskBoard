<script lang="ts">
	import { Board } from '$lib/components/board';
	import { useBoard } from '$api/board';
	import { Spinner } from '$lib/ui/spinner';
	import type { IColumn } from '$api/column';

	const board = useBoard(1);
	let columnItems: IColumn[] = $state([]);

	$effect(() => {
		if (board.data) {
			columnItems = board.data.columns;
		}
	});
</script>

<div class="flex flex-1 flex-col gap-5">
	{#if board.isFetching}
		<div class="flex h-full w-full flex-col items-center justify-center">
			<Spinner class="size-15" />
		</div>
	{:else if board.error}
		<div class="flex h-full w-full flex-col items-center justify-center">
			<h1 class="text-4xl font-bold">
				{board.error.message}
			</h1>
		</div>
	{:else if board.data}
		<h1 class="text-2xl font-semibold">{board.data.title}</h1>
		<Board bind:columnItems boardId={board.data.id} />
	{/if}
</div>
