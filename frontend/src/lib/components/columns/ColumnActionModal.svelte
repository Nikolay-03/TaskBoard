<script lang="ts">
	import { fade } from 'svelte/transition';
	import { DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle } from '$lib/ui/dialog';
	import type { Snippet } from 'svelte';
	import { Button, type ButtonProps } from '$lib/ui/button';
	import { InputField } from '$lib/components/form';
	import { toast } from 'svelte-sonner';
	import { queryClient } from '$api';
	import { type IColumn, useCreateColumn, useDeleteColumn, useUpdateColumn } from '$api/column';
	import { type CreateColumnSchema, createColumnSchema } from './schema';
	import { getRequestErrorMessage } from '$lib/utils';

	type ModeType = 'edit' | 'create';

	interface Props {
		trigger: Snippet<[ButtonProps]>;
		boardId: number;
		defaultValues?: IColumn;
		mode?: ModeType;
	}

	let open = $state(false);

	let { trigger, boardId, mode = 'create', defaultValues }: Props = $props();

	let title = $state<string>('');

	const createColumnMutation = useCreateColumn(boardId);
	const updateColumnMutation = useUpdateColumn();
	const deleteColumnMutation = useDeleteColumn();

	let error = $state<string | null>(null);

	const handleSubmit = async (e: SubmitEvent) => {
		e.preventDefault();
		const data: CreateColumnSchema = {
			title
		};
		const result = createColumnSchema.safeParse(data);
		if (!result.success) {
			error = result.error.issues[0].message;
		} else {
			error = null;
			try {
				if (mode === 'create') {
					await createColumnMutation.mutateAsync({ title });
				} else if (mode === 'edit' && defaultValues?.id) {
					await updateColumnMutation.mutateAsync({ body: { title }, id: defaultValues.id });
				}
				onOpenChange(false);
				await queryClient.invalidateQueries({ queryKey: ['board', boardId] });
			} catch (err) {
				const errorMessage = err instanceof Error ? err.message : '';
				toast.error(errorMessage);
			}
		}
	};
	const handleDeleteColumn = async (e: MouseEvent) => {
		e.stopPropagation();
		try {
			if (mode === 'edit' && defaultValues?.id) {
				await deleteColumnMutation.mutateAsync(defaultValues.id);
				await queryClient.invalidateQueries({ queryKey: ['board', boardId] });
			}
		} catch (err) {
			const errorMessage = getRequestErrorMessage(err);
			toast.error(errorMessage);
		}
	};
	const onOpenChange = (openVal: boolean) => {
		open = openVal;
	};
	$effect(() => {
		if (!open) {
			title = '';
		} else {
			title = defaultValues?.title ?? '';
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
			<DialogTitle>{mode === 'create' ? 'Create column' : 'Edit column'}</DialogTitle>
		</DialogHeader>
		<form onsubmit={handleSubmit} class="flex flex-col gap-3">
			<InputField title="Title" bind:value={title} placeholder="Task title" />
			{#if error}
				<div class="text-sm text-destructive" transition:fade>{error}</div>
			{/if}
			<div class="flex justify-between">
				{#if mode === 'edit'}
					<Button variant="destructive" onclick={handleDeleteColumn}>Delete</Button>
				{/if}
				<Button
					type="submit"
					class="ml-auto"
					disabled={createColumnMutation.isPending || updateColumnMutation.isPending}
				>
					{mode === 'create' ? 'Create' : 'Edit'}
				</Button>
			</div>
		</form>
	</DialogContent>
</Dialog>
