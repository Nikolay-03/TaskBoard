<script lang="ts">
    import {ModeWatcher} from "mode-watcher";
    import {Menu, UserAvatar} from "$lib/components";
    import {MainWrapper} from "$lib/ui";
    import type {Snippet} from "svelte";
    import {navigate} from 'sv-router/generated';
    import {useUser} from "$api/auth";
    import {Spinner} from "$lib/ui/spinner";
    import {Toaster} from "$lib/ui/sonner";

    const userMutation = useUser()

    let {children}: { children: Snippet } = $props();
    $effect(() => {
       if (userMutation.isError){
           navigate('/auth/login')
       }
    })
</script>

<div class="bg-background h-dvh w-dvw p-4 flex gap-4">
    <ModeWatcher/>
    <Toaster/>
    {#if userMutation.isLoading}
        <div class="w-full h-full flex items-center justify-center">
            <Spinner class="size-15"/>
        </div>
    {:else }
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
    {/if}
</div>