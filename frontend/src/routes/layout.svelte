<script lang="ts">
	import { ModeWatcher } from 'mode-watcher';
	import { Menu, UserHead} from '$lib/components';
	import { MainWrapper } from '$lib/ui';
	import type { Snippet } from 'svelte';
	import { navigate } from 'sv-router/generated';
	import { useUser } from '$api/user';
	import { Spinner } from '$lib/ui/spinner';
	import { Toaster } from '$lib/ui/sonner';

	const userMutation = useUser();

	let { children }: { children: Snippet } = $props();
	$effect(() => {
		if (userMutation.isError) {
			navigate('/auth/login');
		}
	});
</script>

<div class="flex h-dvh w-dvw gap-4 bg-background p-4">
	<ModeWatcher />
	<Toaster />
	{#if userMutation.isLoading}
		<div class="flex h-full w-full items-center justify-center">
			<Spinner class="size-15" />
		</div>
	{:else}
		<Menu />
		<section class="flex w-full flex-1 flex-col gap-4 overflow-hidden">
			<div class="relative flex items-center justify-center">
				<h1 class="text-4xl font-bold capitalize">Kanbanned</h1>
				<UserHead className="absolute right-0" />
			</div>
			<MainWrapper>
				{@render children?.()}
			</MainWrapper>
		</section>
	{/if}
</div>
