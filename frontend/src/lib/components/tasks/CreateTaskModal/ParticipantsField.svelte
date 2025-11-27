<script lang="ts">
import {CommandItem} from "$lib/ui/command";
import XIcon from "@lucide/svelte/icons/x";
import {Badge} from "$lib/ui/badge";
import {Button} from "$lib/ui/button";
import {MultiSelect} from "$lib/components";
import type {IBoardMember} from "$api/board";

interface Props {
    participants: number[]
    members?: IBoardMember[]
    open: boolean
    handleOpenChange: (open: boolean) => void
}
let {participants, members, open, handleOpenChange}: Props = $props()
const selectedParticipants = $derived.by(() => {
        return participants.map(pId => members?.find(p => p.userId === pId)) as IBoardMember[]
    }
);
const handleDeleteParticipant = (id: number) => {
    participants = participants.filter(pId => pId !== id)
};
const handleAddParticipant = (id: number) => participants.push(id)
</script>

<div class="flex gap-1 overflow-x-scroll no-scrollbar max-w-[462px] min-h-fit">
    {#each selectedParticipants as participant (participant.userId)}
        <Badge variant="slate">
            {participant.userName}
            <Button onclick={() => handleDeleteParticipant(participant.userId)} variant="clean" size="fit">
                <XIcon/>
            </Button>
        </Badge>
    {/each}
</div>
<MultiSelect {open} {handleOpenChange} heading="Participants">
    {#snippet renderSelected()}
        <div class="p-2 flex flex-wrap gap-1">
            {#each selectedParticipants as participant (participant.userId)}
                <Badge variant="slate">
                    {participant.userName}
                    <Button onclick={() => handleDeleteParticipant(participant.userId)} variant="clean" size="fit">
                        <XIcon/>
                    </Button>
                </Badge>
            {/each}
        </div>
    {/snippet}
    {#snippet renderOptions()}
        {#if members}
            {#each members as participant (participant.userId)}
                <CommandItem onclick={() => handleAddParticipant(participant.userId)}
                             disabled={participants.includes(participant.userId)} value={participant.userName}>
                    <Badge variant="slate">{participant.userName}</Badge>
                </CommandItem>
            {/each}
        {/if}
    {/snippet}
</MultiSelect>