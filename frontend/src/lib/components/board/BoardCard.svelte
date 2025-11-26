<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui/card'
    import type {ICard} from "$lib/types/board";
    import {TaskModal} from "$lib/components/tasks";
    import type {HTMLAttributes} from "svelte/elements";
    import {CardContent} from "$lib/ui/card/index.js";
    import {Badge} from "$lib/ui/badge/index.js";
    import type {ITask} from "$api/task";

    const {title, description, labels}: ITask = $props();
</script>
<TaskModal>
    {#snippet card(props: HTMLAttributes<HTMLDivElement>)}
        <Card {...props}>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </CardHeader>
            <CardContent>
                <div class="flex gap-2 flex-wrap">
                    {#each labels as label (label.id)}
                        <Badge variant={label.color}>{label.name}</Badge>
                    {/each}
                </div>
            </CardContent>
        </Card>
    {/snippet}
</TaskModal>