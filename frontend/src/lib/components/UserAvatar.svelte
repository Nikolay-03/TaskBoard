<script lang="ts">
    import {Avatar, AvatarFallback, AvatarImage} from "$lib/ui/avatar";
    interface Props {
        className?: string;
    }
    const {className}: Props = $props();
</script>

<div class={["flex items-center gap-2", className]}>
    <Avatar>
        <AvatarImage src="https://i.pinimg.com/originals/7f/19/11/7f1911596ca5d6fa54f1584ba618f2d3.jpg" alt="@shadcn"/>
        <AvatarFallback>CN</AvatarFallback>
    </Avatar>
    Shadcn App
</div>