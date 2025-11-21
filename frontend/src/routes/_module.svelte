<script lang="ts">
    import { ModeWatcher } from "mode-watcher";
    import {Menu} from "$lib/components";
</script>
<div class="bg-background h-dvh w-dvw p-5 flex gap-5">
    <ModeWatcher/>
    <Menu/>
    <main class="w-full h-full">
        <slot/>
    </main>
</div>