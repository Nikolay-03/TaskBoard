<script lang="ts">
    import {ModeWatcher} from "mode-watcher";
    import {Menu, UserAvatar} from "$lib/components";
    import {MainWrapper} from "$lib/ui";
</script>
<div class="bg-background h-dvh w-dvw p-4 flex gap-4">
    <ModeWatcher/>
    <Menu/>
    <section class="w-full h-full flex flex-col gap-4">
        <div class="flex items-center justify-center relative">
            <h1 class="font-bold text-4xl">TaskBoard</h1>
            <UserAvatar className="absolute right-0"/>
        </div>
        <MainWrapper>
            <slot/>
        </MainWrapper>
    </section>
</div>