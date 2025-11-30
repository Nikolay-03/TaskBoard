<script lang="ts">
	import { BoardCard } from '$lib/components/board';
	import { useGetFavorites } from '$api/board';
	import { Spinner } from '$lib/ui/spinner';

	const boards = useGetFavorites();
</script>

<div class="flex flex-1 flex-col gap-5">
	<h1 class="text-2xl font-semibold">Your favorite boards</h1>
	{#if boards.isLoading}
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
				<BoardCard {...board} />
			{/each}
		</div>
	{/if}
</div>
