<script lang="ts">
	import { ModeWatcher } from 'mode-watcher';
	import type { Snippet } from 'svelte';
	import { Toaster } from '$lib/ui/sonner';
	import { useUser } from '$api/user';
	import { navigate } from 'sv-router/generated';
	import { Spinner } from '$lib/ui/spinner';

	let { children }: { children: Snippet } = $props();

	const userQuery = useUser();
	$effect(() => {
		if (!userQuery.isLoading && !userQuery.isError) {
			navigate('/');
		}
	});
</script>

<main class="flex min-h-dvh min-w-dvh flex-col items-center justify-center gap-15 bg-background">
	<ModeWatcher />
	<Toaster />
	{#if userQuery.isLoading}
		<Spinner class="size-15" />
	{:else}
		<h1 class="text-8xl font-bold capitalize">Kanbanned</h1>
		{@render children?.()}
	{/if}
</main>
