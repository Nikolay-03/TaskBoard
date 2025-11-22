<script lang="ts">
    import {Card, CardHeader, CardTitle, CardDescription} from '$lib/ui'
    import type {ICard} from "$lib/types/board";
    const {title, description}: ICard = $props();
</script>
<Card>
    <CardHeader>
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
    </CardHeader>
</Card>