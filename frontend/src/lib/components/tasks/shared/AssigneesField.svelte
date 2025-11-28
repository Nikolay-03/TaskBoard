<script lang="ts">
    import {CommandItem} from "$lib/ui/command";
    import XIcon from "@lucide/svelte/icons/x";
    import {Badge} from "$lib/ui/badge";
    import {Button} from "$lib/ui/button";
    import {MultiSelect} from "$lib/components";
    import type {IUser} from "$api/user";
    import {TaskBadges} from "$lib/components/tasks";

    interface Props {
        assignees: number[]
        members?: IUser[]
        open: boolean
        handleOpenChange: (open: boolean) => void
    }
    let {assignees = $bindable(), members, open, handleOpenChange}: Props = $props()
    const selectedAssignees = $derived.by(() => {
            return assignees.map(aId => members?.find(m => m.id === aId)) as IUser[]
        }
    );
    const handleDeleteAssignee = (id: number) => {
        assignees = assignees.filter(aId => aId !== id)
    };
    const handleAddAssignee = (id: number) => assignees.push(id)
</script>

<TaskBadges items={selectedAssignees} onDelete={handleDeleteAssignee}/>
<MultiSelect {open} {handleOpenChange} heading="Assignees">
    {#snippet renderSelected()}
        <TaskBadges items={selectedAssignees} onDelete={handleDeleteAssignee} className="p-2"/>
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