<script lang="ts">
    import {type BadgeVariant } from '$lib/ui/badge';
    import type {IUserBadgeItem} from "./types";
    import UserBadge from "./UserBadge.svelte";

    interface Props {
        users: IUserBadgeItem[];
        className?: string;
        defaultVariant?: BadgeVariant;
        onDelete?: (id: number) => void;
    }
    let { users, onDelete, defaultVariant = 'slate', className }: Props = $props();
</script>

<div class={['flex flex-wrap gap-2', className]}>
    {#each users as user (user.id)}
       <UserBadge {onDelete} {...user} color={defaultVariant}/>
    {/each}
</div>
