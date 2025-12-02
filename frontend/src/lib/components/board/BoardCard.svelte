<script lang="ts">
	import { StarIcon } from '@lucide/svelte';
	import { CardDescription, CardFooter, CardHeader, CardTitle, Card } from '$lib/ui/card/index.js';
	import { type IBoard, useAddBoardToFavorites, useDeleteBoardFromFavorites } from '$api/board';
	import { formatUtcDate, getRequestErrorMessage } from '$lib/utils';
	import { navigate } from 'sv-router/generated';
	import { Button } from '$lib/ui/button';
	import { toast } from 'svelte-sonner';
	let { title, description, createdAt, id, isFavorite }: IBoard = $props();
	const formattedDate = formatUtcDate(createdAt);

	const addToFavorites = useAddBoardToFavorites();
	const deleteFromFavorites = useDeleteBoardFromFavorites();
	const handleToggleFavorite = async (e: MouseEvent) => {
		e.stopPropagation();
		try {
			if (isFavorite) {
				await deleteFromFavorites.mutateAsync(id);
			} else {
				await addToFavorites.mutateAsync(id);
			}
		} catch (e) {
			toast.error(getRequestErrorMessage(e));
		}
	};
</script>

<Card
	class="h-[200px] w-full cursor-pointer"
	onclick={() => navigate(`/boards/${id}`, { viewTransition: true })}
>
	<CardHeader>
		<div class="flex justify-between">
			<CardTitle class="text-xl">{title}</CardTitle>
			<Button variant="clean" size="fit" onclick={handleToggleFavorite}>
				<StarIcon class={[{ 'fill-foreground': isFavorite }]} />
			</Button>
		</div>
		<CardDescription class="text-base">{description}</CardDescription>
	</CardHeader>
	<CardFooter class="mt-auto ml-auto text-muted-foreground">
		Created at: {formattedDate}
	</CardFooter>
</Card>
