<script>
	import { Button } from '$lib/ui/button';
	import { navigate } from 'sv-router/generated';
</script>

<div class="flex h-full w-full flex-col items-center justify-center gap-4">
	<h1 class="text-9xl font-bold">Oops!</h1>
	<h2 class="text-xl font-semibold">404 - Page not found</h2>
	<Button onclick={() => navigate('/')}>Return to dashboard</Button>
</div>
