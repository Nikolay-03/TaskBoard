<script lang="ts">
	import { fade } from 'svelte/transition';
	import { RegistrationSchema } from '$lib/components/auth/schema';
	import { Input } from '$lib/ui/input';
	import { Card, CardContent, CardHeader } from '$lib/ui/card';
	import { Button } from '$lib/ui/button';
	import { CardTitle } from '$lib/ui/card/index.js';
	import { Label } from '$lib/ui/label';
	import { EMAIL_PLACEHOLDER, PASSWORD_PLACEHOLDER } from './constants';
	import { useRegister } from '$api/auth';
	import { navigate } from 'sv-router/generated';

	const registrationUser = useRegister();
	let email = $state('');
	let password = $state('');
	let name = $state('');
	let error = $state();
	const handleSubmit = async (e: SubmitEvent) => {
		e.preventDefault();
		const result = RegistrationSchema.safeParse({ name, email, password });
		if (!result.success) {
			error = result.error.issues[0].message;
		} else {
			error = null;
			await registrationUser.mutateAsync({
				name,
				password,
				email
			});
			navigate('/');
		}
	};
</script>

<Card class="w-[450px] gap-2">
	<CardHeader>
		<CardTitle class="text-center text-xl">Sign up</CardTitle>
	</CardHeader>
	<CardContent>
		<form onsubmit={handleSubmit} class="flex flex-col gap-3">
			<div class="flex flex-col gap-3">
				<div class="flex flex-col gap-2">
					<Label for="name">Your name</Label>
					<Input id="name" bind:value={name} placeholder="John Doe" />
				</div>
				<div class="flex flex-col gap-2">
					<Label for="email">Email</Label>
					<Input id="email" bind:value={email} placeholder={EMAIL_PLACEHOLDER} />
				</div>
				<div class="flex flex-col gap-2">
					<Label for="password">Password</Label>
					<Input
						id="password"
						bind:value={password}
						placeholder={PASSWORD_PLACEHOLDER}
						type="password"
					/>
				</div>
			</div>
			{#if error}
				<div class="text-sm text-destructive" transition:fade>{error}</div>
			{/if}
			<div class="flex justify-between">
				<span class="self-end text-sm"
					>Already have an account? <a
						href="/auth/login"
						class="text-primary underline-offset-4 hover:underline"
					>
						Sign in</a
					></span
				>
				<Button type="submit" class="w-fit" disabled={registrationUser.isLoading}>Sign up</Button>
			</div>
		</form>
	</CardContent>
</Card>
