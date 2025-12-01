<script lang="ts">
	import { fade } from 'svelte/transition';
	import { Dialog, DialogHeader, DialogTitle, DialogTrigger, DialogContent } from '$lib/ui/dialog';
	import { type Snippet } from 'svelte';
	import type { HTMLAttributes } from 'svelte/elements';
	import { Spinner } from '$lib/ui/spinner';
	import { useGetTask } from '$api/task';
	import TaskBadges from './shared/TaskBadges.svelte';
	import { Label } from '$lib/ui/label';
	import {UserBadges} from "$lib/components/profile";

	interface Props {
		card: Snippet<[HTMLAttributes<HTMLDivElement>]>;
		id: number;
	}

	let { card, id }: Props = $props();

	let taskQuery = useGetTask(id);
	let data = $derived(taskQuery.data);
	let open = $state(false);

	$effect(() => {
		if (!open) return;
		taskQuery.refetch();
	});
</script>

<Dialog bind:open>
	<DialogTrigger>
		{#snippet child({ props })}
			{@render card(props)}
		{/snippet}
	</DialogTrigger>
	<DialogContent>
		{#if taskQuery.isLoading}
			<div class="flex h-[400px] w-full items-center justify-center">
				<Spinner class="size-10" />
			</div>
		{:else if taskQuery.error}
			<p class="text-red-500" transition:fade>Ошибка: {taskQuery.error.message}</p>
		{:else if data}
			<DialogHeader>
				<DialogTitle>{data.title}</DialogTitle>
			</DialogHeader>
			<div class="flex flex-col gap-1">
				<Label class="text-base">Description</Label>
				<p class="text-sm text-muted-foreground">{data.description}</p>
			</div>
			<div class="flex flex-col gap-1">
				<Label class="text-base">Labels</Label>
				<TaskBadges items={data.labels} />
			</div>
			<div class="flex flex-col gap-1">
				<Label class="text-base">Assignees</Label>
				<UserBadges users={data.assignees} />
			</div>
			<div class="flex flex-col gap-1">
				<Label class="text-base">Participants</Label>
				<UserBadges users={data.participants} />
			</div>
		{/if}
	</DialogContent>
</Dialog>
