<script lang="ts">
    import type {Snippet} from "svelte";

    let { children }: { children: Snippet } = $props();
</script>

<main class="bg-primary-foreground border rounded-2xl p-5 flex flex-col h-full w-full overflow-auto">
    {@render children?.()}
</main>