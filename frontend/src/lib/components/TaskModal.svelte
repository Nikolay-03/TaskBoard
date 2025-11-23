<script lang="ts">
    import {Dialog, DialogTitle, DialogTrigger} from "$lib/ui";
    let { children } = $props();
</script>

<Dialog>
    <DialogTrigger>
        {@render children?.()}
    </DialogTrigger>
</Dialog>