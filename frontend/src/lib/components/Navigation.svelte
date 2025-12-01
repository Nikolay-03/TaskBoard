<script lang="ts">
	import { navigate } from 'sv-router/generated';
	import { menuItems, type IMenuItem } from '$lib/shared';
	import { Button } from '$lib/ui/button/index.js';
</script>

{#snippet menuItem(item: IMenuItem)}
	<li>
		<Button
			variant="ghost"
			size="lg"
			class="w-full justify-start gap-3 text-base font-semibold"
			onclick={() => navigate(item.href, {viewTransition: true})}
		>
			<svelte:component this={item.icon} size={30} class="size-6"/>
			{item.name}
		</Button>
	</li>
{/snippet}
<nav>
	<ul class="flex flex-col gap-3">
		{#each menuItems as item (item.id)}
			{@render menuItem(item)}
		{/each}
	</ul>
</nav>
