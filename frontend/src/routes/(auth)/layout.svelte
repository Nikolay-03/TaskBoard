<script lang="ts">
    import {ModeWatcher} from "mode-watcher";
    import type {Snippet} from "svelte";
    import {Toaster} from "$lib/ui/sonner";
    import {useUser} from "$api/auth";
    import {navigate} from "sv-router/generated";
    import {Spinner} from "$lib/ui/spinner";

    let {children}: { children: Snippet } = $props();

    const userMutation = useUser()
    $effect(() => {
        if (!userMutation.isLoading && !userMutation.isError) {
            navigate('/')
        }
    })
</script>
<main class="min-w-dvh min-h-dvh bg-background flex flex-col items-center justify-center gap-15">
    <ModeWatcher/>
    <Toaster/>
    {#if userMutation.isLoading}
        <Spinner class="size-15"/>
    {:else }
        <h1 class="text-8xl font-bold">PlanForge</h1>
        {@render children?.()}
    {/if}
</main>