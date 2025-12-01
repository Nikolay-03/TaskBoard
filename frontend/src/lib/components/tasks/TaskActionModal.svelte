<script lang="ts">
	import { fade } from 'svelte/transition';
	import { DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle } from '$lib/ui/dialog';
	import type { Snippet } from 'svelte';
	import { Button, type ButtonProps } from '$lib/ui/button';
	import { useLabels } from '$api/label';
	import { useMembers } from '$api/board';
	import AssigneesField from '$lib/components/tasks/shared/AssigneesField.svelte';
	import ParticipantsField from '$lib/components/tasks/shared/ParticipantsField.svelte';
	import LabelsField from '$lib/components/tasks/shared/LabelsField.svelte';
	import { DateField, InputField, TextAreaField } from '$lib/components/form';
	import { type CalendarDate, getLocalTimeZone, today, parseDate } from '@internationalized/date';
	import { toast } from 'svelte-sonner';
	import { createTaskSchema, type CreateTaskSchema } from '$lib/components/tasks/shared/schema';
	import { type ITask, useCreateTask, useDeleteTask, useUpdateTask } from '$api/task';
	import { queryClient } from '$api';

	type ModeType = 'edit' | 'create';

	interface Props {
		trigger: Snippet<[ButtonProps]>;
		columnId: number;
		boardId: number;
		defaultValues?: ITask;
		mode?: ModeType;
	}

	let open = $state(false);
	let labelsModalOpen = $state<boolean>(false);
	let participantsModalOpen = $state<boolean>(false);
	let assigneesModalOpen = $state<boolean>(false);

	let { trigger, columnId, boardId, mode = 'create', defaultValues }: Props = $props();

	let title = $state<string>('');
	let description = $state<string>('');
	let dueDate = $state<CalendarDate | undefined>();
	let labels = $state<number[]>([]);
	let participants = $state<number[]>([]);
	let assignees = $state<number[]>([]);
	$effect(() => {
		if (open) {
			title = defaultValues?.title ?? '';
			description = defaultValues?.description ?? '';
			dueDate = defaultValues?.dueDate
				? parseDate(defaultValues.dueDate)
				: today(getLocalTimeZone());
			labels = defaultValues?.labels.map((l) => l.id) ?? [];
			participants = defaultValues?.participants.map((p) => p.id) ?? [];
			assignees = defaultValues?.assignees.map((a) => a.id) ?? [];
		}
	});
	const createTaskMutation = useCreateTask();
	const updateTaskMutation = useUpdateTask();
	const deleteTaskMutation = useDeleteTask();

	const membersQuery = useMembers(boardId);
	const labelsQuery = useLabels();

	let error = $state<string | null>(null);

	const handleSubmit = async (e: SubmitEvent) => {
		e.preventDefault();
		const data: CreateTaskSchema = {
			title,
			description,
			dueDate: dueDate?.toString() ?? '',
			labels,
			participants,
			assignees
		};
		const result = createTaskSchema.safeParse(data);
		if (!result.success) {
			error = result.error.issues[0].message;
		} else {
			error = null;
			try {
				if (mode === 'create') {
					await createTaskMutation.mutateAsync({ columnId, body: data });
				} else if (mode === 'edit' && defaultValues?.id) {
					await updateTaskMutation.mutateAsync({ id: defaultValues.id, body: data });
					await queryClient.invalidateQueries({queryKey: ['task', defaultValues.id]})
				}
				onOpenChange(false);
				await queryClient.invalidateQueries({ queryKey: ['board', boardId] });
			} catch (err) {
				const errorMessage = err instanceof Error ? err.message : '';
				toast.error(errorMessage);
			}
		}
	};

	const handleDeleteTask = async (e: MouseEvent) => {
		e.stopPropagation();
		try {
			if (mode === 'edit' && defaultValues?.id) {
				await deleteTaskMutation.mutateAsync(defaultValues.id);
				await queryClient.invalidateQueries({ queryKey: ['board', boardId] });
			}
		} catch (err) {
			const errorMessage = err instanceof Error ? err.message : '';
			toast.error(errorMessage);
		}
	};
	const onOpenChange = (openVal: boolean) => {
		open = openVal;
	};
	const handleLabelModalOpenChange = (open: boolean) => {
		labelsModalOpen = open;
	};
	const handleAssigneesModalOpenChange = (open: boolean) => {
		assigneesModalOpen = open;
	};
	const handleParticipantsModalOpenChange = (open: boolean) => {
		participantsModalOpen = open;
	};
</script>

<Dialog {open} {onOpenChange}>
	<DialogTrigger onclick={(e) => e.stopPropagation()}>
		{#snippet child({ props })}
			{@render trigger(props)}
		{/snippet}
	</DialogTrigger>
	<DialogContent class="min-h-[400px] max-w-[512px]">
		<DialogHeader>
			<DialogTitle>{mode === 'create' ? 'Create task' : 'Edit task'}</DialogTitle>
		</DialogHeader>
		<form onsubmit={handleSubmit} class="flex flex-col gap-3">
			<InputField title="Title" bind:value={title} placeholder="Task title" />
			<TextAreaField title="Description" bind:value={description} placeholder="Task description" />
			<DateField title="Deadline date" bind:value={dueDate} />
			<Button onclick={() => (labelsModalOpen = true)} variant="outline">Select labels</Button>
			<LabelsField
				open={labelsModalOpen}
				handleOpenChange={handleLabelModalOpenChange}
				labelsOpts={labelsQuery.data}
				bind:labels
			/>
			<Button onclick={() => (assigneesModalOpen = true)} variant="outline">
				Select assignees
			</Button>
			<AssigneesField
				open={assigneesModalOpen}
				handleOpenChange={handleAssigneesModalOpenChange}
				members={membersQuery.data}
				bind:assignees
			/>
			<Button onclick={() => (participantsModalOpen = true)} variant="outline">
				Select participants
			</Button>
			<ParticipantsField
				open={participantsModalOpen}
				handleOpenChange={handleParticipantsModalOpenChange}
				members={membersQuery.data}
				bind:participants
			/>
			{#if error}
				<div class="text-sm text-destructive" transition:fade>{error}</div>
			{/if}
			<div class="flex justify-between">
				{#if mode === 'edit'}
					<Button variant="destructive" onclick={(e) => handleDeleteTask(e)}>Delete</Button>
				{/if}
				<Button
					type="submit"
					class="ml-auto"
					disabled={createTaskMutation.isPending || updateTaskMutation.isPending}
				>
					{mode === 'create' ? 'Create' : 'Edit'}
				</Button>
			</div>
		</form>
	</DialogContent>
</Dialog>
