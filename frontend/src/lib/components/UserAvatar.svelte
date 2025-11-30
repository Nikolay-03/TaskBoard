<script lang="ts">
	import { Avatar, AvatarFallback, AvatarImage } from '$lib/ui/avatar';
	import { useUser } from '$api/auth';
	import {Skeleton} from "$lib/ui/skeleton";
	interface Props {
		className?: string;
	}
	const { className }: Props = $props();
	const user = useUser();
</script>

<div class={['flex items-center gap-2', className]}>
	<Avatar>
		<AvatarImage
			src="https://i.pinimg.com/originals/7f/19/11/7f1911596ca5d6fa54f1584ba618f2d3"
			alt="@shadcn"
		/>
		<AvatarFallback>
			<Skeleton class="size-full"/>
		</AvatarFallback>
	</Avatar>
	{#if user.isLoading || !user.data}
		<Skeleton class="w-[150px] h-3"/>
		{:else}
		{user.data.name}
	{/if}
</div>
