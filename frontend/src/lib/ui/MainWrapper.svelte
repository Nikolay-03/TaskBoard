<script lang="ts">
	import type { Snippet } from 'svelte';

	let { children }: { children: Snippet } = $props();
</script>

<main
	class="flex h-full w-full flex-col overflow-auto rounded-2xl border bg-primary-foreground p-5"
>
	{@render children?.()}
</main>
