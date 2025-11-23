<script lang="ts">
    import {Dialog, DialogHeader, DialogTitle, DialogTrigger, DialogContent, DialogDescription} from "$lib/ui/dialog";
    import {type Snippet} from "svelte";
    import type {ITask} from "$api/task";
    import type {HTMLAttributes} from "svelte/elements";
    import {client} from "$api/client";
    import {Spinner} from "$lib/ui/spinner";
    let {card}: { card: Snippet<[HTMLAttributes<HTMLDivElement>]> } = $props();
    const getData = client.createRequest()({
        method: "GET",
        endpoint: "/todos/1",
    });
    let open = $state(false)
    let loading = $state(false);
    let data = $state();
    let error = $state();
    const mockTask: ITask = {
        id: '1',
        title: 'task',
        createdAt: '2025-10-12',
        description: 'some description'
    }
    $effect(() => {
        if (!open) return;

        loading = true;
        data = null;
        error = null;
        (async () => {
            const res = await getData.send();
            loading = false;
            data = res.data;
            error = res.error;
        })();
    })
</script>

<Dialog {open} onOpenChange={() => open = !open}>
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
                <DialogTitle>{mockTask.title}</DialogTitle>
            </DialogHeader>
            this is task
            <DialogDescription>
                {mockTask.description}
            </DialogDescription>
        {/if}
    </DialogContent>
</Dialog>