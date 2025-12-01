<script lang="ts">
	import { Avatar, AvatarFallback, AvatarImage } from '$lib/ui/avatar';
	import {Skeleton} from "$lib/ui/skeleton";
	interface Props {
		className?: string;
		avatar?: string | null
	}
	const { className, avatar }: Props = $props();
	
	let loadingStatus = $state<'loading' | 'loaded' | 'error'>('loading');
	let previousAvatar = $state(avatar);
	
	$effect(() => {
		if (avatar !== previousAvatar) {
			loadingStatus = 'loading';
			previousAvatar = avatar;
		}
	});
</script>

<Avatar class={[className]} bind:loadingStatus>
	<AvatarImage
		src={avatar}
		alt="User avatar"
	/>
	<AvatarFallback>
		<Skeleton class="size-full"/>
	</AvatarFallback>
</Avatar>
