<script lang="ts">
    import {CommandItem} from "$lib/ui/command";
    import XIcon from "@lucide/svelte/icons/x";
    import {Badge} from "$lib/ui/badge";
    import {Button} from "$lib/ui/button";
    import {MultiSelect} from "$lib/components";
    import type {IUser} from "$api/user";

    interface Props {
        assignees: number[]
        members?: IUser[]
        open: boolean
        handleOpenChange: (open: boolean) => void
    }
    let {assignees, members, open, handleOpenChange}: Props = $props()
    const selectedAssignees = $derived.by(() => {
            return assignees.map(aId => members?.find(m => m.id === aId)) as IUser[]
        }
    );
    const handleDeleteAssignee = (id: number) => {
        assignees = assignees.filter(aId => aId !== id)
    };
    const handleAddAssignee = (id: number) => assignees.push(id)
</script>

<div class="flex gap-1 overflow-x-scroll no-scrollbar max-w-[462px] min-h-fit">
    {#each selectedAssignees as assignee (assignee.id)}
        <Badge variant="slate">
            {assignee.name}
            <Button onclick={() => handleDeleteAssignee(assignee.id)} variant="clean" size="fit">
                <XIcon/>
            </Button>
        </Badge>
    {/each}
</div>
<MultiSelect {open} {handleOpenChange} heading="Assignees">
    {#snippet renderSelected()}
        <div class="p-2 flex flex-wrap gap-1">
            {#each selectedAssignees as assignee (assignee.id)}
                <Badge variant="slate">
                    {assignee.name}
                    <Button onclick={() => handleDeleteAssignee(assignee.id)} variant="clean" size="fit">
                        <XIcon/>
                    </Button>
                </Badge>
            {/each}
        </div>
    {/snippet}
    {#snippet renderOptions()}
        {#if members}
            {#each members as member (member.id)}
                <CommandItem onclick={() => handleAddAssignee(member.id)}
                             disabled={assignees.includes(member.id)} value={member.name}>
                    <Badge variant="slate">{member.name}</Badge>
                </CommandItem>
            {/each}
        {/if}
    {/snippet}
</MultiSelect>