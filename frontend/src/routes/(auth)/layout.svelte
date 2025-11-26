<script lang="ts">
	import { ModeWatcher } from "mode-watcher";
    import type {Snippet} from "svelte";
    import {Toaster} from "$lib/ui/sonner";

    let {children}: {children: Snippet} = $props();
</script>
<main class="min-w-dvh min-h-dvh bg-background flex flex-col items-center justify-center gap-15">
    <h1 class="text-8xl font-bold">PlanForge</h1>
    <ModeWatcher/>
    <Toaster/>
    {@render children?.()}
</main>