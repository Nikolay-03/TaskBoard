<script lang="ts">
    import { CommandItem } from '$lib/ui/command';
    import { MultiSelect } from '$lib/components';
    import type { IUser } from '$api/user';
    import {UserBadge, UserBadges} from "$lib/components/profile";

    interface Props {
        members: number[];
        users?: IUser[];
        open: boolean;
        handleOpenChange: (open: boolean) => void;
    }
    let { members = $bindable(), users, open, handleOpenChange }: Props = $props();
    const selectedParticipants = $derived.by(() => {
        return members?.map((pId) => users?.find((p) => p.id === pId)) as IUser[];
    });
    const handleDeleteParticipant = (id: number) => {
        members = members.filter((pId) => pId !== id);
    };
    const handleAddParticipant = (id: number) => members.push(id);
</script>

<UserBadges users={selectedParticipants} onDelete={handleDeleteParticipant} />
<MultiSelect {open} {handleOpenChange} heading="Participants">
    {#snippet renderSelected()}
        <UserBadges users={selectedParticipants} onDelete={handleDeleteParticipant} className="p-2"/>
    {/snippet}
    {#snippet renderOptions()}
        {#if users}
            {#each users as user (user.id)}
                <CommandItem
                        onclick={() => handleAddParticipant(user.id)}
                        disabled={members.includes(user.id)}
                        value={user.name}
                >
                    <UserBadge {...user}/>
                </CommandItem>
            {/each}
        {/if}
    {/snippet}
</MultiSelect>
