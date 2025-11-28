<script lang="ts">
    import {Dialog, DialogHeader, DialogTitle, DialogTrigger, DialogContent, DialogDescription} from "$lib/ui/dialog";
    import {type Snippet} from "svelte";
    import type {HTMLAttributes} from "svelte/elements";
    import {Spinner} from "$lib/ui/spinner";
    import {api} from "$api";
    import {useGetTask} from "$api/task";
    interface Props {
        card: Snippet<[HTMLAttributes<HTMLDivElement>]>
        id: number
    }
    let {card, id}: Props = $props();
    const taskQuery = useGetTask(id)
    let open = $state(false);
    let loading = $state(false);
    let data = $state();
    let error = $state();
    $effect(() => {
        if (!open) return;

        loading = true;
        data = null;
        error = null;
        (async () => {
            const res = await api.get('/boards/1')
            loading = false;
            data = res.data;
            error = res.error;
        })();
    })
    $effect(() => {
        if (!open) return;
        taskQuery.refetch();
    });
</script>

<Dialog bind:open>
    <DialogTrigger>
        {#snippet child({props})}
            {@render card(props)}
        {/snippet}
    </DialogTrigger>
    <DialogContent class="min-h-[400px]">
        {#if loading}
            <div class="flex items-center justify-center w-full h-full">
                <Spinner class="size-10"/>
            </div>
        {:else if error}
            <p class="text-red-500">Ошибка: {String(error)}</p>
        {:else if data}
            <DialogHeader>
                <DialogTitle></DialogTitle>
            </DialogHeader>
            this is task
            <DialogDescription>

            </DialogDescription>
        {/if}
    </DialogContent>
</Dialog>