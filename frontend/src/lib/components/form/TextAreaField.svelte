<script lang="ts">
	import { Label } from '$lib/ui/label';
	import { Textarea } from '$lib/ui/textarea';
	import type { WithElementRef, WithoutChildren } from '$lib/utils';
	import type { HTMLTextareaAttributes } from 'svelte/elements';

	type Props = WithoutChildren<WithElementRef<HTMLTextareaAttributes>> & {
		title: string;
	};
	let { title, value = $bindable(), ...textAreaProps }: Props = $props();
	const id = $props.id();
</script>

<div class="flex flex-col gap-2">
	<Label for={`${title}-${id}`}>{title}</Label>
	<Textarea id={`${title}-${id}`} bind:value {...textAreaProps} />
</div>
