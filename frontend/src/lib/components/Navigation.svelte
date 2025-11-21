<script lang="ts">
    import {menuItems, type IMenuItem} from '$lib/shared'
    import {Button} from "$lib/ui/button/index.js";
</script>
{#snippet menuItem(item: IMenuItem)}
    <li>
        <a href={item.href}>
            <Button variant="ghost" size="lg" class="w-full justify-start gap-2">
                <svelte:component this={item.icon} size={20}/>
                {item.name}
            </Button>
        </a>
    </li>
{/snippet}
<nav>
    <ul class="flex flex-col gap-3">
        {#each menuItems as item}
            {@render menuItem(item)}
        {/each}
    </ul>
</nav>