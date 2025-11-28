<script lang="ts">
	import {Trash2} from "@lucide/svelte";
	import { Card, CardHeader, CardTitle, CardDescription } from '$lib/ui/card';
	import { TaskBadges, TaskModal } from '$lib/components/tasks';
	import type { HTMLAttributes } from 'svelte/elements';
	import { CardContent } from '$lib/ui/card/index.js';
	import {type ITask, useDeleteTask} from '$api/task';
	import {queryClient} from "$api";
	import {toast} from "svelte-sonner";
	import {Button} from "$lib/ui/button";

	const { title, description, labels, id, assignees, boardId }: ITask = $props();

	const deleteTaskMutation = useDeleteTask(id)

	const handleDeleteTask = async (e: MouseEvent) => {
		e.stopPropagation()
		try {
			await deleteTaskMutation.mutateAsync()
			await queryClient.invalidateQueries({queryKey: ['board', boardId]})
		}
		catch (err) {
			const errorMessage = err instanceof Error ? err.message : '';
			toast.error(errorMessage);
		}
	}
</script>

<TaskModal {id}>
	{#snippet card(props: HTMLAttributes<HTMLDivElement>)}
		<Card {...props}>
			<CardHeader>
				<div class="flex justify-between gap-2">
					<CardTitle>{title}</CardTitle>
					<Button variant="clean" size="fit" onclick={(e) => handleDeleteTask(e)}>
						<Trash2 class="text-destructive"/>
					</Button>
				</div>
				<CardDescription>{description}</CardDescription>
			</CardHeader>
			<CardContent class="flex flex-col gap-4">
				<TaskBadges items={labels} />
				<TaskBadges items={assignees} />
			</CardContent>
		</Card>
	{/snippet}
</TaskModal>
