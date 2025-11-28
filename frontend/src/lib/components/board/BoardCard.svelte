<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui/card'
    import {TaskModal} from "$lib/components/tasks";
    import type {HTMLAttributes} from "svelte/elements";
    import {CardContent} from "$lib/ui/card/index.js";
    import {Badge} from "$lib/ui/badge/index.js";
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
                <div class="flex gap-2 flex-wrap">
                    {#each labels as label (label.id)}
                        <Badge variant={label.color}>{label.name}</Badge>
                    {/each}
                </div>
                <div class="flex gap-2 flex-wrap">
                    {#each assignees as assignee (assignee.id)}
                        <Badge variant="slate">{assignee.name}</Badge>
                    {/each}
                </div>
            </CardContent>
        </Card>
    {/snippet}
</TaskModal>