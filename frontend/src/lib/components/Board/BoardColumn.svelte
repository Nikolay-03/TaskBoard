<script lang="ts">
    import {Card, CardHeader, CardTitle} from '$lib/ui'
    import type {IColumn} from "$lib/types/board";
    import {flip} from 'svelte/animate';
    import {dndzone} from 'svelte-dnd-action';
    import {CardContent} from "$lib/ui/index.js";
    import {BoardCard} from "$lib/components";

    interface Props {
        column: IColumn,
        handleDndConsiderCards: (id: number, e: any) => void;
        handleDndFinalizeCards: (id: number, e: any) => void;
        flipDurationMs: number;
    }

    const {column, handleDndFinalizeCards, handleDndConsiderCards, flipDurationMs}: Props = $props();
</script>
<Card class="h-full border-dotted border-2">
    <CardHeader>
        <CardTitle class="text-center">{column.name}</CardTitle>
    </CardHeader>
    <CardContent class="min-w-[350px] h-full px-3">
        <div class="h-full flex flex-col gap-3" use:dndzone={{items: column.items, flipDurationMs, dropTargetClasses: ["!outline-none"]}}
             on:consider={(e) => handleDndConsiderCards(column.id, e)}
             on:finalize={(e) => handleDndFinalizeCards(column.id, e)}>
            {#each column.items as item (item.id)}
                <div animate:flip={{duration: flipDurationMs}}>
                    <BoardCard {...item}/>
                </div>
            {/each}
        </div>
    </CardContent>
</Card>