<script lang="ts">
    import PlusIcon from '@lucide/svelte/icons/plus';
    import {Button, type ButtonProps} from '$lib/ui/button';
    import {flip} from 'svelte/animate';

    let {flipDurationMs, buttonProps}: { flipDurationMs: number, buttonProps: ButtonProps } = $props();
</script>

{#each [null] as dummy (dummy)}
    <div animate:flip={{ duration: flipDurationMs }}>
        <Button class="text-base h-15 w-full" {...buttonProps}>
            <PlusIcon class="size-6"/>
            Add task
        </Button>
    </div>
{/each}