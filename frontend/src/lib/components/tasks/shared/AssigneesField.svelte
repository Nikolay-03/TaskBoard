<script lang="ts">
	import { CommandItem } from '$lib/ui/command';
	import { MultiSelect } from '$lib/components';
	import type { IUser } from '$api/user';
	import {UserBadge, UserBadges} from "$lib/components/profile";

	interface Props {
		assignees: number[];
		members?: IUser[];
		open: boolean;
		handleOpenChange: (open: boolean) => void;
	}
	let { assignees = $bindable(), members, open, handleOpenChange }: Props = $props();
	const selectedAssignees = $derived.by(() => {
		return assignees.map((aId) => members?.find((m) => m.id === aId)) as IUser[];
	});
	const handleDeleteAssignee = (id: number) => {
		assignees = assignees.filter((aId) => aId !== id);
	};
	const handleAddAssignee = (id: number) => assignees.push(id);
</script>

<UserBadges users={selectedAssignees} onDelete={handleDeleteAssignee} />
<MultiSelect {open} {handleOpenChange} heading="Assignees">
	{#snippet renderSelected()}
		<UserBadges users={selectedAssignees} onDelete={handleDeleteAssignee} className="p-2"/>
	{/snippet}
	{#snippet renderOptions()}
		{#if members}
			{#each members as member (member.id)}
				<CommandItem
					onclick={() => handleAddAssignee(member.id)}
					disabled={assignees.includes(member.id)}
					value={member.name}
				>
					<UserBadge {...member}/>
				</CommandItem>
			{/each}
		{/if}
	{/snippet}
</MultiSelect>
