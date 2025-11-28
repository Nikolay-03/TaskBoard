<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui/card'
    import {TaskBadges, TaskModal} from "$lib/components/tasks";
    import type {HTMLAttributes} from "svelte/elements";
    import {CardContent} from "$lib/ui/card/index.js";
    import type {ITask} from "$api/task";

    const {title, description, labels, id, assignees}: ITask = $props();
</script>
<TaskModal {id}>
    {#snippet card(props: HTMLAttributes<HTMLDivElement>)}
        <Card {...props}>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </CardHeader>
            <CardContent class="flex flex-col gap-2">
                <TaskBadges items={labels}/>
                <TaskBadges items={assignees}/>
            </CardContent>
        </Card>
    {/snippet}
</TaskModal>