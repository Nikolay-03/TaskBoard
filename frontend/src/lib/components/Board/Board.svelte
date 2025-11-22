<script lang="ts">
    import {dndzone} from 'svelte-dnd-action';
    import {BoardColumn} from "$lib/components";
    import {flip} from 'svelte/animate';
    import type {IColumn} from "$lib/types/board";

    interface Props {
        columnItems: IColumn[]
    }

    let {columnItems}: Props = $props();

    function handleDndConsiderColumns(e) {
        columnItems = e.detail.items;
    }

    function handleDndFinalizeColumns(e) {
        columnItems = e.detail.items;
    }

    const flipDurationMs = 200;

    function handleDndConsiderCards(cid, e) {
        const colIdx = columnItems.findIndex(c => c.id === cid);
        columnItems[colIdx].items = e.detail.items;
        columnItems = [...columnItems];
    }

    function handleDndFinalizeCards(cid, e) {
        const colIdx = columnItems.findIndex(c => c.id === cid);
        columnItems[colIdx].items = e.detail.items;
        columnItems = [...columnItems];
    }
</script>
<section class="w-full flex h-fit gap-4 flex-grow overflow-x-auto"
         use:dndzone={{items:columnItems, flipDurationMs, type:'columns', dropTargetClasses: ["!outline-none"]}}
         on:consider={handleDndConsiderColumns} on:finalize={handleDndFinalizeColumns}>
    {#each columnItems as column (column.id)}
        <div animate:flip={{duration: flipDurationMs}}>
            <BoardColumn column={column} {handleDndConsiderCards} {handleDndFinalizeCards}
                         flipDurationMs={flipDurationMs}/>
        </div>
    {/each}
</section>