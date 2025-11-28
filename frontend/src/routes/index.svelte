<script lang="ts">
    import {Board} from "$lib/components/board";
    import {useBoard} from "$api/board";
    import {Spinner} from "$lib/ui/spinner";

    const board = useBoard(1);
</script>

<div class="flex flex-col gap-5 flex-1">
    {#if board.isLoading}
        <div class="flex flex-col items-center justify-center h-full w-full">
            <Spinner class="size-15" />
        </div>
    {:else if board.error}
        <div class="flex flex-col items-center justify-center h-full w-full">
            <h1 class="text-4xl font-bold">
                {board.error.message}
            </h1>
        </div>
    {:else if board.data}
        <h1 class="font-semibold text-2xl">{board.data.title}</h1>
        <Board columnItems={board.data.columns}/>
    {/if}
</div>
