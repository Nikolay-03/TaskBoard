<script lang="ts">
    import type {IBoardMember} from "$api/board";
    import {CommandItem} from "$lib/ui/command";
    import XIcon from "@lucide/svelte/icons/x";
    import {Badge} from "$lib/ui/badge";
    import {Button} from "$lib/ui/button";
    import {MultiSelect} from "$lib/components";

    interface Props {
        assignees: number[]
        members?: IBoardMember[]
        open: boolean
        handleOpenChange: (open: boolean) => void
    }
    let {assignees, members, open, handleOpenChange}: Props = $props()
    const selectedAssignees = $derived.by(() => {
            return assignees.map(aId => members?.find(m => m.userId === aId)) as IBoardMember[]
        }
    );
    const handleDeleteAssignee = (id: number) => {
        assignees = assignees.filter(aId => aId !== id)
    };
    const handleAddAssignee = (id: number) => assignees.push(id)
</script>

<div class="flex gap-1 overflow-x-scroll no-scrollbar max-w-[462px] min-h-fit">
    {#each selectedAssignees as assignee (assignee.userId)}
        <Badge variant="slate">
            {assignee.userName}
            <Button onclick={() => handleDeleteAssignee(assignee.userId)} variant="clean" size="fit">
                <XIcon/>
            </Button>
        </Badge>
    {/each}
</div>
<MultiSelect {open} {handleOpenChange} heading="Assignees">
    {#snippet renderSelected()}
        <div class="p-2 flex flex-wrap gap-1">
            {#each selectedAssignees as assignee (assignee.userId)}
                <Badge variant="slate">
                    {assignee.userName}
                    <Button onclick={() => handleDeleteAssignee(assignee.userId)} variant="clean" size="fit">
                        <XIcon/>
                    </Button>
                </Badge>
            {/each}
        </div>
    {/snippet}
    {#snippet renderOptions()}
        {#if members}
            {#each members as member (member.userId)}
                <CommandItem onclick={() => handleAddAssignee(member.userId)}
                             disabled={assignees.includes(member.userId)} value={member.userName}>
                    <Badge variant="slate">{member.userName}</Badge>
                </CommandItem>
            {/each}
        {/if}
    {/snippet}
</MultiSelect>