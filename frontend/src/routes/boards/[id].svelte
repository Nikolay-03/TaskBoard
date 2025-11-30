<script lang="ts">
    import {PencilIcon, StarIcon} from '@lucide/svelte'
    import { Board } from '$lib/components/board';
    import {useAddBoardToFavorites, useBoard, useDeleteBoardFromFavorites} from '$api/board';
    import { Spinner } from '$lib/ui/spinner';
    import type { IColumn } from '$api/column';
    import {Button} from "$lib/ui/button";
    import BoardActionModal from "$lib/components/board/BoardActionModal.svelte";
    import { route } from 'sv-router/generated';
    import {toast} from "svelte-sonner";
    import {getRequestErrorMessage} from "$lib/utils";

    const board = useBoard(Number(route.params?.id));
    const addToFavorites = useAddBoardToFavorites()
    const deleteFromFavorites = useDeleteBoardFromFavorites()

    let columnItems: IColumn[] = $state([]);

    const handleToggleFavorite = async (e: MouseEvent) => {
        e.stopPropagation()
        try{
            if(board.data){
                if(board.data.isFavorite){
                    await deleteFromFavorites.mutateAsync(board.data.id)
                }
                else {
                    await addToFavorites.mutateAsync(board.data.id)
                }
            }
        }
        catch (e) {
            toast.error(getRequestErrorMessage(e))
        }
    }
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
        <div class="flex flex-col gap-y-1">
            <div class="flex justify-between">
                <div class="flex items-center gap-x-3">
                    <h1 class="text-2xl font-semibold">{board.data.title}</h1>
                    <BoardActionModal mode="edit" defaultValues={{...board.data}}>
                        {#snippet trigger(props)}
                            <Button {...props} variant="clean" size="fit">
                                <PencilIcon class="text-muted-foreground" />
                            </Button>
                        {/snippet}
                    </BoardActionModal>
                </div>
                <Button
                        variant="clean"
                        size="fit"
                        onclick={handleToggleFavorite}
                >
                    <StarIcon class={["size-8",{"fill-white": board.data.isFavorite}]}/>
                </Button>
            </div>
            <h2 class="text-base text-muted-foreground">{board.data.description}</h2>
        </div>
        <Board bind:columnItems boardId={board.data.id} />
    {/if}
</div>
