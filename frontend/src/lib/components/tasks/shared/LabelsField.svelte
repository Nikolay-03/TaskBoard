<script lang="ts">
    import type {ILabel} from "$api/label";
    import {Badge} from "$lib/ui/badge";
    import {MultiSelect} from "$lib/components";
    import {CommandItem} from "$lib/ui/command";
    import {TaskBadges} from "$lib/components/tasks";

    interface Props {
        labels: number[]
        labelsOpts?: ILabel[]
        open: boolean
        handleOpenChange: (open: boolean) => void
    }

    let {labels = $bindable(), labelsOpts, open, handleOpenChange}: Props = $props()
    const selectedLabels = $derived.by(() => {
            return labels.map(lId => labelsOpts?.find(l => l.id === lId)) as ILabel[]
        }
    );
    const handleDeleteLabel = (id: number) => {
        labels = labels.filter(lId => lId !== id)
    };
    const handleAddLabel = (id: number) => labels.push(id)

</script>
<TaskBadges items={selectedLabels} onDelete={handleDeleteLabel}/>
<MultiSelect {open} {handleOpenChange} heading="Labels">
    {#snippet renderSelected()}
        <TaskBadges items={selectedLabels} onDelete={handleDeleteLabel} className="p-2"/>
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