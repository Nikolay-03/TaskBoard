<script lang="ts">
    import {Card, CardHeader, CardTitle, CardContent} from '$lib/ui/card'
    import {flip} from 'svelte/animate';
    import {dndzone} from 'svelte-dnd-action';
    import {BoardCard} from "$lib/components/board";
    import type {IColumn} from "$api/column";
    import {AddTaskButton} from "$lib/components/tasks";

    interface Props {
        column: IColumn,
        handleDndConsiderCards: (id: number, e: CustomEvent) => void;
        handleDndFinalizeCards: (id: number, e: CustomEvent) => void;
        flipDurationMs: number;
    }

    const {column, handleDndFinalizeCards, handleDndConsiderCards, flipDurationMs}: Props = $props();
</script>
<Card class="h-full border-dashed border-2">
    <CardHeader>
        <CardTitle class="text-center">{column.title}</CardTitle>
    </CardHeader>
    <CardContent class="w-[350px] h-full px-3 flex flex-col gap-3">
        <div class="flex flex-col gap-3 min-h-5"
             use:dndzone={{items: column.tasks, flipDurationMs, dropTargetClasses: ["!outline-none"]}}
             on:consider={(e) => handleDndConsiderCards(column.id, e)}
             on:finalize={(e) => handleDndFinalizeCards(column.id, e)}>
            {#each column.tasks as item (item.id)}
                <div animate:flip={{duration: flipDurationMs}}>
                    <BoardCard {...item}/>
                </div>
            {/each}
           <AddTaskButton {flipDurationMs}/>
        </div>
    </CardContent>
</Card>