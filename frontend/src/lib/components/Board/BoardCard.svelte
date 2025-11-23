<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui/card'
    import type {ICard} from "$lib/types/board";
    import {TaskModal} from "$lib/components";
    import type {HTMLAttributes} from "svelte/elements";
    import {CardContent} from "$lib/ui/card/index.js";
    import {Badge} from "$lib/ui/badge/index.js";
    import {mockLabels} from "$api/label";

    const {title, description}: ICard = $props();
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
                    {#each mockLabels as label (label.id)}
                        <Badge>{label.name}</Badge>
                    {/each}
                </div>
            </CardContent>
        </Card>
    {/snippet}
</TaskModal>