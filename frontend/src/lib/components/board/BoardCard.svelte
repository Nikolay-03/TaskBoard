<script lang="ts">
	import { PencilIcon } from '@lucide/svelte';
	import { Card, CardHeader, CardTitle, CardDescription } from '$lib/ui/card';
	import { TaskActionModal, TaskBadges, TaskModal } from '$lib/components/tasks';
	import type { HTMLAttributes } from 'svelte/elements';
	import { CardContent } from '$lib/ui/card/index.js';
	import { type ITask } from '$api/task';
	import { Button } from '$lib/ui/button';

	let task: ITask = $props();
	let { id, columnId, boardId, title, description, labels, assignees } = task;
</script>

<TaskModal {id}>
	{#snippet card(props: HTMLAttributes<HTMLDivElement>)}
		<Card {...props}>
			<CardHeader>
				<div class="flex justify-between gap-2">
					<CardTitle>{title}</CardTitle>
					<div class="flex gap-3">
						<TaskActionModal {columnId} {boardId} mode="edit" defaultValues={{ ...task }}>
							{#snippet trigger(props)}
								<Button {...props} variant="clean" size="fit">
									<PencilIcon class="text-muted-foreground" />
								</Button>
							{/snippet}
						</TaskActionModal>
					</div>
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
