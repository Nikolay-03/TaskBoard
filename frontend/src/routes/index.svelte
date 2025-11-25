<script lang="ts">
    import {Board} from "$lib/components";
    import {api} from "$api/index";
    import type {IBoard} from "$api/board";
    import {Spinner} from "$lib/ui/spinner";

    let loading = $state(false);
    let data = $state<IBoard | null>();
    let error = $state();
    $effect(() => {
        loading = true;
        data = null;
        error = null;
        (async () => {
            const res = await api.get<IBoard>('/boards/1')
            loading = false;
            data = res;
        })();
    })
    let boardColumns = $derived(data?.columns);
</script>
<div class="flex flex-col gap-5 flex-1">
    <h1 class="font-semibold text-2xl">{data?.title}</h1>
    {#if loading}
        <div class="flex flex-col items-center justify-center h-full w-full">
            <Spinner class="size-15"/>
        </div>
        {:else if boardColumns}
        <Board columnItems={boardColumns}/>
    {/if}
</div>