<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui/card'
    import type {ICard} from "$lib/types/board";
    import {TaskModal} from "$lib/components";
    import type {HTMLAttributes} from "svelte/elements";

    const {title, description}: ICard = $props();
</script>
<TaskModal>
    {#snippet card(props: HTMLAttributes<HTMLDivElement>)}
        <Card {...props}>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
                <CardDescription>{description}</CardDescription>
            </CardHeader>
        </Card>
    {/snippet}
</TaskModal>