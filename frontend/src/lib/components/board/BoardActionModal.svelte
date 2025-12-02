<script lang="ts">
	import { fade } from 'svelte/transition';
	import {UsersIcon} from '@lucide/svelte'
	import { DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle } from '$lib/ui/dialog';
	import type { Snippet } from 'svelte';
	import { Button, type ButtonProps } from '$lib/ui/button';
	import { InputField } from '$lib/components/form';
	import { toast } from 'svelte-sonner';
	import { queryClient } from '$api';
	import { type BoardSchema, boardSchema } from './schema';
	import { getRequestErrorMessage } from '$lib/utils';
	import {type IBoard, useCreateBoard, useDeleteBoard, useMembers} from '$api/board';
	import { useUpdateBoard } from '$api/board/index.js';
	import { navigate } from 'sv-router/generated';
	import BoardMembersField from "$lib/components/board/BoardMembersField.svelte";
	import {useUsers} from "$api/user";

	type ModeType = 'edit' | 'create';

	interface Props {
		trigger: Snippet<[ButtonProps]>;
		defaultValues?: IBoard;
		mode?: ModeType;
	}

	let open = $state(false);

	let { trigger, mode = 'create', defaultValues }: Props = $props();

	let membersModalOpen = $state<boolean>(false);

	let title = $state<string>('');
	let description = $state<string | undefined>();
	let members = $state<number[]>([])

	const createBoardMutation = useCreateBoard();
	const updateBoardMutation = useUpdateBoard();
	const deleteBoardMutation = useDeleteBoard();

	const users = useUsers();
	const membersQuery = useMembers(defaultValues?.id)

	let error = $state<string | null>(null);

	const handleSubmit = async (e: SubmitEvent) => {
		e.preventDefault();
		const body = {
			title,
			members,
			...(description ? { description } : {})
		};
		const result = boardSchema.safeParse(body);
		if (!result.success) {
			error = result.error.issues[0].message;
		} else {
			error = null;
			try {
				if (mode === 'create') {
					const response = await createBoardMutation.mutateAsync(body);
					navigate(`/boards/${response.id}`, { viewTransition: true });
				} else if (mode === 'edit' && defaultValues?.id) {
					await updateBoardMutation.mutateAsync({
						body,
						id: defaultValues.id
					});
					await queryClient.invalidateQueries({ queryKey: ['board', defaultValues.id] });
					await queryClient.invalidateQueries({ queryKey: ['members', defaultValues.id] });
				}
				onOpenChange(false);
			} catch (err) {
				const errorMessage = err instanceof Error ? err.message : '';
				toast.error(errorMessage);
			}
		}
	};
	const handleDeleteBoard = async (e: MouseEvent) => {
		e.stopPropagation();
		try {
			if (mode === 'edit' && defaultValues?.id) {
				await deleteBoardMutation.mutateAsync(defaultValues.id);
				navigate('/');
				await queryClient.invalidateQueries({ queryKey: ['boards'] });
			}
		} catch (err) {
			const errorMessage = getRequestErrorMessage(err);
			toast.error(errorMessage);
		}
	};
	const onOpenChange = (openVal: boolean) => {
		open = openVal;
	};
	const handleMembersModalOpenChange = (open: boolean) => {
		membersModalOpen = open;
	};
	$effect(() => {
		if (open) {
			title = defaultValues?.title ?? '';
			description = defaultValues?.description;
			members = membersQuery.data?.map(member => member.id) ?? []
		}
	});
</script>

<Dialog {open} {onOpenChange}>
	<DialogTrigger onclick={(e) => e.stopPropagation()}>
		{#snippet child({ props })}
			{@render trigger(props)}
		{/snippet}
	</DialogTrigger>
	<DialogContent class="max-w-[512px]">
		<DialogHeader>
			<DialogTitle>{mode === 'create' ? 'Create board' : 'Edit board'}</DialogTitle>
		</DialogHeader>
		<form onsubmit={handleSubmit} class="flex flex-col gap-3">
			<InputField title="Title" bind:value={title} placeholder="Task title" />
			<InputField title="Description" bind:value={description} placeholder="Task description" />
			<Button onclick={() => (membersModalOpen = true)} variant="outline">
				Select members
				<UsersIcon/>
			</Button>
			<BoardMembersField
					open={membersModalOpen}
					handleOpenChange={handleMembersModalOpenChange}
					users={users.data}
					bind:members
			/>
			{#if error}
				<div class="text-sm text-destructive" transition:fade>{error}</div>
			{/if}
			<div class="flex justify-between">
				{#if mode === 'edit'}
					<Button variant="destructive" onclick={handleDeleteBoard}>Delete</Button>
				{/if}
				<Button
					type="submit"
					class="ml-auto"
					disabled={createBoardMutation.isPending || updateBoardMutation.isPending}
				>
					{mode === 'create' ? 'Create' : 'Edit'}
				</Button>
			</div>
		</form>
	</DialogContent>
</Dialog>
