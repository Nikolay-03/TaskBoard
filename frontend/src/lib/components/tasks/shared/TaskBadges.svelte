<script lang="ts">
    import XIcon from '@lucide/svelte/icons/x'
    import {Badge, type BadgeVariant} from "$lib/ui/badge";
    import {Button} from "$lib/ui/button";

    interface BadgeItem {
        id: number;
        name: string;
        color?: BadgeVariant;
    }

    interface Props {
        items: BadgeItem[],
        onDelete?: (id: number) => void
        className?: string
        defaultVariant?: BadgeVariant
    }

    let {items,onDelete, defaultVariant = "slate", className}: Props = $props()
</script>

<div class={["flex gap-2 flex-wrap", className]}>
    {#each items as item (item.id)}
        <Badge variant={item.color ?? defaultVariant}>
            {item.name}
            {#if onDelete}
                <Button onclick={() => onDelete(item.id)} variant="clean" size="fit">
                    <XIcon/>
                </Button>
            {/if}
        </Badge>
    {/each}
</div>
