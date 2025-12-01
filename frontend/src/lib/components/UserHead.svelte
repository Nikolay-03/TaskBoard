<script lang="ts">
    import {Avatar, AvatarFallback, AvatarImage} from '$lib/ui/avatar';
    import {useUser} from '$api/user';
    import {Skeleton} from "$lib/ui/skeleton";
    import {navigate} from "sv-router/generated";
    import {Button} from "$lib/ui/button";

    interface Props {
        className?: string;
    }

    const {className}: Props = $props();
    const user = useUser();
</script>

<Button
        class={['flex items-center gap-2', className]}
        onclick={() => navigate('/me', {viewTransition: true})}
        variant="outline"
        size="lg"
>
    <Avatar>
        <AvatarImage
                src={user.data?.avatar}
                alt="User avatar"
        />
        <AvatarFallback>
            <Skeleton class="size-full"/>
        </AvatarFallback>
    </Avatar>
    {#if user.isLoading || !user.data}
        <Skeleton class="w-[150px] h-3"/>
    {:else}
        {user.data.name}
    {/if}
</Button>
