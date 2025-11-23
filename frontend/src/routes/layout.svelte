<script lang="ts">
    import {ModeWatcher} from "mode-watcher";
    import {Menu, UserAvatar} from "$lib/components";
    import {MainWrapper} from "$lib/ui";
    import type {Snippet} from "svelte";

    let {children}: { children: Snippet } = $props();
</script>
<div class="bg-background h-dvh w-dvw p-4 flex gap-4">
    <ModeWatcher/>
    <Menu/>
    <section class="w-full flex-1 flex flex-col gap-4 overflow-hidden">
        <div class="flex items-center justify-center relative">
            <h1 class="font-bold text-4xl">PlanForge</h1>
            <UserAvatar className="absolute right-0"/>
        </div>
        <MainWrapper>
            {@render children?.()}
        </MainWrapper>
    </section>
</div>