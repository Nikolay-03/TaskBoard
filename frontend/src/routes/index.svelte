<script lang="ts">
	import {CirclePlus} from '@lucide/svelte'
	import {BoardActionModal, BoardCard} from '$lib/components/board';
	import {useGetBoards} from '$api/board';
	import { Spinner } from '$lib/ui/spinner';
	import {Button} from "$lib/ui/button";

	const boards = useGetBoards();

</script>

<div class="flex flex-1 flex-col gap-5">
	<h1 class="text-2xl font-semibold">Your boards</h1>
	{#if boards.isFetching}
		<div class="flex h-full w-full flex-col items-center justify-center">
			<Spinner class="size-15" />
		</div>
	{:else if boards.error}
		<div class="flex h-full w-full flex-col items-center justify-center">
			<h1 class="text-4xl font-bold">
				{boards.error.message}
			</h1>
		</div>
	{:else if boards.data}
		<div class="grid grid-cols-[repeat(auto-fit,minmax(400px,1fr))] gap-4">
		{#each boards.data as board (board.id)}
				<BoardCard {...board}/>
			{/each}
			<BoardActionModal>
				{#snippet trigger(props)}
					<Button
							{...props}
							class="flex w-full h-full min-h-[200px] flex-col gap-3 border-2 border-dashed px-3"
							variant="outline"
					>
						<CirclePlus class="size-10 text-muted-foreground" />
					</Button>
				{/snippet}
			</BoardActionModal>
		</div>
	{/if}
</div>
