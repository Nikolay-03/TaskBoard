<script lang="ts">
    import {Badge} from "$lib/ui/badge";
    import type {IUserBadgeItem} from "$lib/components/profile/types";
    import {UserAvatar} from "$lib/components";
    import {XIcon} from "@lucide/svelte";
    import {Button} from "$lib/ui/button";
    let {name, onDelete, avatar, color, id}: IUserBadgeItem = $props()
</script>
<Badge variant={color}>
    <UserAvatar avatar={avatar}/>
    {name}
    {#if onDelete}
        <Button onclick={() => onDelete(id)} variant="clean" size="fit">
            <XIcon />
        </Button>
    {/if}
</Badge>