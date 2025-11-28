<script lang="ts">
	import { Label } from '$lib/ui/label';
	import { Input, type InputProps } from '$lib/ui/input';

	type Props = InputProps & {
		title: string;
	};
	let { title, value = $bindable(), ...inputProps }: Props = $props();
	const id = $props.id();
</script>

<div class="flex flex-col gap-2">
	<Label for={`${title}-${id}`}>{title}</Label>
	<Input id={`${title}-${id}`} bind:value {...inputProps} />
</div>
