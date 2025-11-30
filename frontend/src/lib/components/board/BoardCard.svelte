<script lang="ts">
    import {CardDescription, CardFooter, CardHeader, CardTitle, Card} from "$lib/ui/card/index.js";
    import type {IBoard} from "$api/board";
    import {formatUtcDate} from "$lib/utils";
    import {navigate} from "sv-router/generated";
    let {title, description, createdAt, id}: IBoard = $props()

    const formattedDate = formatUtcDate(createdAt)
</script>

<Card class="w-full h-[200px] cursor-pointer" onclick={() => navigate(`/boards/${id}`, {viewTransition: true})}>
    <CardHeader>
        <CardTitle class="text-xl">{title}</CardTitle>
        <CardDescription class="text-base">{description}</CardDescription>
    </CardHeader>
    <CardFooter class="text-muted-foreground ml-auto mt-auto">
        Created at: {formattedDate}
    </CardFooter>
</Card>