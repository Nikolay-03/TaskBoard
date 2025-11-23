<script lang="ts">
    import PlusIcon from '@lucide/svelte/icons/plus';
    import { Button } from '$lib/ui/button';
    import {flip} from 'svelte/animate';
    let {flipDurationMs}: {flipDurationMs: number} = $props();
</script>

{#each [null] as _ (0)}
    <div animate:flip={{ duration: flipDurationMs }}>
        <Button class="text-base h-15 w-full">
            <PlusIcon class="size-6" />
            Add task
        </Button>
    </div>
{/each}
