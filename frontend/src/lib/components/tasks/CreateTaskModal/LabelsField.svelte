<script lang="ts">
    import type {ILabel} from "$api/label";
    import XIcon from "@lucide/svelte/icons/x";
    import {Badge} from "$lib/ui/badge";
    import {MultiSelect} from "$lib/components";
    import {Button} from "$lib/ui/button";
    import {CommandItem} from "$lib/ui/command";

    interface Props {
        labels: number[]
        labelsOpts?: ILabel[]
        open: boolean
        handleOpenChange: (open: boolean) => void
    }

    let {labels, labelsOpts, open, handleOpenChange}: Props = $props()
    const selectedLabels = $derived.by(() => {
            return labels.map(lId => labelsOpts?.find(l => l.id === lId)) as ILabel[]
        }
    );
    const handleDeleteLabel = (id: number) => {
        labels = labels.filter(lId => lId !== id)
    };
    const handleAddLabel = (id: number) => labels.push(id)

</script>
<div class="flex gap-1 overflow-x-scroll no-scrollbar max-w-[462px] min-h-fit">
    {#each selectedLabels as label (label.id)}
        <Badge variant={label.color}>
            {label.name}
            <Button onclick={() => handleDeleteLabel(label.id)} variant="clean" size="fit">
                <XIcon/>
            </Button>
        </Badge>
    {/each}
</div>
<MultiSelect {open} {handleOpenChange} heading="Labels">
    {#snippet renderSelected()}
        <div class="p-2 flex flex-wrap gap-1">
            {#each selectedLabels as label (label.id)}
                <Badge variant={label.color}>
                    {label.name}
                    <Button onclick={() => handleDeleteLabel(label.id)} variant="clean" size="fit">
                        <XIcon/>
                    </Button>
                </Badge>
            {/each}
        </div>
    {/snippet}
    {#snippet renderOptions()}
        {#if labelsOpts}
            {#each labelsOpts as label (label.id)}
                <CommandItem onclick={() => handleAddLabel(label.id)}
                             disabled={labels.includes(label.id)} value={label.name}>
                    <Badge variant={label.color}>{label.name}</Badge>
                </CommandItem>
            {/each}
        {/if}
    {/snippet}
</MultiSelect>