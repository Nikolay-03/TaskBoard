<script lang="ts">
	import { Avatar, AvatarFallback, AvatarImage } from '$lib/ui/avatar';
	import {Skeleton} from "$lib/ui/skeleton";
	interface Props {
		className?: string;
		avatar?: string | null
	}
	const { className, avatar }: Props = $props();
</script>

	<Avatar class={[className]}>
		<AvatarImage
			src={avatar}
			alt="User avatar"
		/>
		<AvatarFallback>
			<Skeleton class="size-full"/>
		</AvatarFallback>
	</Avatar>
