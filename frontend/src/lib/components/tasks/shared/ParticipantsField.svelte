<script lang="ts">
	import { CommandItem } from '$lib/ui/command';
	import { MultiSelect } from '$lib/components';
	import type { IUser } from '$api/user';
	import {UserBadge, UserBadges} from "$lib/components/profile";

	interface Props {
		participants: number[];
		members?: IUser[];
		open: boolean;
		handleOpenChange: (open: boolean) => void;
	}
	let { participants = $bindable(), members, open, handleOpenChange }: Props = $props();
	const selectedParticipants = $derived.by(() => {
		return participants.map((pId) => members?.find((p) => p.id === pId)) as IUser[];
	});
	const handleDeleteParticipant = (id: number) => {
		participants = participants.filter((pId) => pId !== id);
	};
	const handleAddParticipant = (id: number) => participants.push(id);
</script>

<UserBadges users={selectedParticipants} onDelete={handleDeleteParticipant} />
<MultiSelect {open} {handleOpenChange} heading="Participants">
	{#snippet renderSelected()}
		<UserBadges users={selectedParticipants} onDelete={handleDeleteParticipant} className="p-2"/>
	{/snippet}
	{#snippet renderOptions()}
		{#if members}
			{#each members as participant (participant.id)}
				<CommandItem
					onclick={() => handleAddParticipant(participant.id)}
					disabled={participants.includes(participant.id)}
					value={participant.name}
				>
					<UserBadge {...participant}/>
				</CommandItem>
			{/each}
		{/if}
	{/snippet}
</MultiSelect>
