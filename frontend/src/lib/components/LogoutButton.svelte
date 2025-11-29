<script lang="ts">
	import { LogOut } from '@lucide/svelte';
	import { Button } from '$lib/ui/button';
	import { useLogout } from '$api/auth';
	import { navigate } from 'sv-router/generated';

	const logoutMutation = useLogout();
	const handleLogout = async () => {
		try {
			await logoutMutation.mutateAsync();
			navigate('/auth/login');
		} catch (e) {
			console.error(e);
		}
	};
</script>

<Button variant="outline" size="icon" onclick={handleLogout}>
	<LogOut />
</Button>
