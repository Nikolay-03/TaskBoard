<script lang="ts">
    import XIcon from "@lucide/svelte/icons/x";
    import {DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle} from "$lib/ui/dialog";
    import type {Snippet} from "svelte";
    import {Button, type ButtonProps} from "$lib/ui/button";
    import {Label} from "$lib/ui/label";
    import {Input} from "$lib/ui/input/index.js";
    import {type ILabel, useLabels} from "$api/label";
    import {Badge} from "$lib/ui/badge";
    import {CommandItem} from "$lib/ui/command";
    import {MultiSelect} from "$lib/components";

    interface Props {
        trigger: Snippet<[ButtonProps]>,
        columnId: number
    }

    let labelsModalOpen = $state<boolean>(false)
    let title = $state();
    let description = $state();
    let dueDate = $state();
    let labels = $state<number[]>([]);
    let participants = $state();
    let assignees = $state();
    let {trigger, columnId}: Props = $props();
    const labelsQuery = useLabels()
    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault()
        console.log($state.snapshot(labels))
    }
    const selectedLabels = $derived.by(() => {
            return labels.map(lId => labelsQuery.data?.find(l => l.id === lId)) as ILabel[]
        }
    );
    const handleDelete = (id: number) => {
        labels = labels.filter(l => l !== id)
    };
    const handleAdd = (id: number) => labels.push(id)
    const handleOpenChange = (open: boolean) => {
        labelsModalOpen = open;
    };
</script>
<Dialog>
    <DialogTrigger>
        {#snippet child({props})}
            {@render trigger(props)}
        {/snippet}
    </DialogTrigger>
    <DialogContent class="min-h-[400px] max-w-[512px]">
        <DialogHeader>
            <DialogTitle>Create task</DialogTitle>
        </DialogHeader>
        <form onsubmit={handleSubmit} class="flex flex-col gap-3">
            <div class="flex flex-col gap-2">
                <Label for="title">Title</Label>
                <Input id="title" bind:value={title} placeholder="Task title"/>
            </div>
            <div class="flex flex-col gap-2">
                <Label for="description">Description</Label>
                <Input id="description" bind:value={description} placeholder="Task description"/>
            </div>
            <div class="flex flex-col gap-2">
                <Label for="dueDate">Deadline date</Label>
                <Input id="dueDate" bind:value={dueDate} placeholder="Task deadline date" type="date"/>
            </div>
            <Button onclick={() => labelsModalOpen = true}>
                Select labels
            </Button>
            <div class="flex gap-1 overflow-x-scroll no-scrollbar max-w-[462px] min-h-fit">
                {#each selectedLabels as label (label.id)}
                    <Badge variant={label.color}>
                        {label.name}
                        <Button onclick={() => handleDelete(label.id)} variant="clean" size="fit">
                            <XIcon/>
                        </Button>
                    </Badge>
                {/each}
            </div>
            <MultiSelect open={labelsModalOpen} handleOpenChange={handleOpenChange} heading="Labels">
                {#snippet renderSelected()}
                    <div class="p-2 flex flex-wrap gap-1">
                        {#each selectedLabels as label (label.id)}
                            <Badge variant={label.color}>
                                {label.name}
                                <Button onclick={() => handleDelete(label.id)} variant="clean" size="fit">
                                    <XIcon/>
                                </Button>
                            </Badge>
                        {/each}
                    </div>
                {/snippet}
                {#snippet renderOptions()}
                    {#if labelsQuery.data}
                        {#each labelsQuery.data as label (label.id)}
                            <CommandItem onclick={() => handleAdd(label.id)}
                                         disabled={labels.includes(label.id)} value={label.name}>
                                <Badge variant={label.color}>{label.name}</Badge>
                            </CommandItem>
                        {/each}
                    {/if}
                {/snippet}
            </MultiSelect>
            <div class="flex flex-col gap-2">
                <Label for="participants">Participants</Label>
                <Input id="participants" bind:value={participants} placeholder="Task participants"/>
            </div>
            <div class="flex flex-col gap-2">
                <Label for="assignees">Assignees</Label>
                <Input id="assignees" bind:value={assignees} placeholder="Task assignees"/>
            </div>
            <Button type="submit">
                Create
            </Button>
        </form>
    </DialogContent>
</Dialog>