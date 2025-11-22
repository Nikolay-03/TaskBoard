<script lang="ts">
import {Board} from "$lib/components";
import type {IColumn} from "$lib/types/board";
let board: IColumn[] = [
    {
        id: 1,
        name: "TODO",
        items: [
            {id: 41, title: "task", description: "description"},
            {id: 42, title: "item42", description: "description"},
            {id: 43, title: "item43", description: "description"},
            {id: 44, title: "item44", description: "description"},
            {id: 45, title: "item45", description: "description"},
            {id: 46, title: "item46", description: "description"},
            {id: 47, title: "item47", description: "description"},
            {id: 48, title: "item48", description: "description"},
            {id: 49, title: "item49", description: "description"}
        ]
    },
    {
        id: 2,
        name: "IN PROGRESS",
        items: []
    },
    {
        id: 3,
        name: "DONE",
        items: []
    },
];
let boardColumns = $state(board);
</script>
<div class="flex flex-col gap-5">
    <h1 class="font-semibold text-2xl">Board</h1>
    <Board columnItems={boardColumns}/>
</div>