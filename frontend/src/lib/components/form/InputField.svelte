<script lang="ts">
    import {Label} from "$lib/ui/label";
    import {Input, type InputProps} from "$lib/ui/input";

    type Props = InputProps & {
        title: string
    }
    let {title, value, ...inputProps}: Props = $props();
</script>

<div class="flex flex-col gap-2">
    <Label for={title}>{title}</Label>
    <Input id={title} bind:value {...inputProps}/>
</div>