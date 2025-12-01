<script lang="ts">
    import {PencilIcon} from '@lucide/svelte';
    import {Card, CardHeader, CardTitle} from '$lib/ui/card';
    import {TaskActionModal, TaskBadges, TaskModal} from '$lib/components/tasks/index';
    import type {HTMLAttributes} from 'svelte/elements';
    import {CardContent} from '$lib/ui/card';
    import {type ITask} from '$api/task';
    import {Button} from '$lib/ui/button';
    import {UserBadges} from "$lib/components/profile";

    let {
        id,
        columnId,
        boardId,
        title,
        description,
        labels,
        assignees,
        position,
        createdAt,
        updatedAt,
        dueDate,
        participants
    }: ITask = $props();
</script>

<TaskModal {id}>
    {#snippet card(props: HTMLAttributes<HTMLDivElement>)}
        <Card {...props}>
            <CardHeader>
                <div class="flex justify-between gap-2">
                    <CardTitle>{title}</CardTitle>
                    <div class="flex gap-3">
                        <TaskActionModal
                                {columnId}
                                {boardId}
                                mode="edit"
                                defaultValues={{
		                            id,
		                            columnId,
		                            boardId,
		                            title,
		                            description,
		                            labels,
		                            assignees,
		                            position,
		                            createdAt,
		                            updatedAt,
		                            dueDate,
		                            participants
	                            }}
                        >
                            {#snippet trigger(props)}
                                <Button {...props} variant="clean" size="fit">
                                    <PencilIcon class="text-muted-foreground"/>
                                </Button>
                            {/snippet}
                        </TaskActionModal>
                    </div>
                </div>
            </CardHeader>
            <CardContent class="flex flex-col gap-10">
                <TaskBadges items={labels}/>
                <UserBadges users={assignees}/>
            </CardContent>
        </Card>
    {/snippet}
</TaskModal>
