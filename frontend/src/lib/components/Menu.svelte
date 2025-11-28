<script lang="ts">
	import { LogoutButton, Navigation, ThemeSwitcher } from '$lib/components';
</script>

<div class="flex min-w-[260px] flex-col rounded-2xl border bg-primary-foreground p-5">
	<Navigation />
	<div class="mt-auto flex items-center justify-between">
		<LogoutButton />
		<ThemeSwitcher />
	</div>
</div>
