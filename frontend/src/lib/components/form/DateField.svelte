<script lang="ts">
	import CalendarIcon from '@lucide/svelte/icons/calendar';
	import { getLocalTimeZone, today, type CalendarDate } from '@internationalized/date';
	import { Label } from '$lib/ui/label';
	import { PopoverTrigger, Popover, PopoverContent } from '$lib/ui/popover';
	import { Button } from '$lib/ui/button';
	import { Calendar } from '$lib/ui/calendar';

	interface Props {
		title: string;
		value?: CalendarDate;
		placeholder?: string;
	}

	let { value = $bindable(), placeholder = 'Select date', title }: Props = $props();
	const id = $props.id();
	let open = $state(false);
</script>

<div class="flex flex-col gap-2">
	<Label for="{id}-date">{title}</Label>
	<Popover bind:open>
		<PopoverTrigger id="{id}-date">
			{#snippet child({ props })}
				<Button {...props} variant="outline" class="justify-between font-normal">
					{value ? value.toDate(getLocalTimeZone()).toLocaleDateString() : placeholder}
					<CalendarIcon />
				</Button>
			{/snippet}
		</PopoverTrigger>
		<PopoverContent class="w-auto overflow-hidden p-0" align="center">
			<Calendar
				type="single"
				bind:value
				captionLayout="dropdown"
				minValue={today(getLocalTimeZone())}
				onValueChange={() => {
					open = false;
				}}
			/>
		</PopoverContent>
	</Popover>
</div>
