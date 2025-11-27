<script lang="ts">
    import {fade} from 'svelte/transition';
    import {LoginSchema} from "$lib/components/auth/schema";
    import {Input} from "$lib/ui/input";
    import {Card, CardContent, CardHeader} from "$lib/ui/card";
    import {Button} from "$lib/ui/button";
    import {CardTitle} from "$lib/ui/card/index.js";
    import {useLogin} from "$api/auth";
    import {navigate} from "sv-router/generated";
    import {Label} from "$lib/ui/label";
    import {toast} from "svelte-sonner";

    const loginMutation = useLogin();
    let email = $state('');
    let password = $state('');
    let error = $state();
    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault()
        const result = LoginSchema.safeParse({email, password});
        if (!result.success) {
            error = result.error.issues[0].message
        } else {
            error = null
            try {
                await loginMutation.mutateAsync({ email, password });
                navigate('/');
            } catch (err) {
                const errorMessage = err instanceof Error ? err.message : "";
                toast.error(errorMessage)
            }
        }
    }
</script>
<Card class="w-[450px] gap-2">
    <CardHeader>
        <CardTitle class="text-xl text-center">Sign in</CardTitle>
    </CardHeader>
    <CardContent>
        <form onsubmit={handleSubmit} class="flex flex-col gap-3">
            <div class="flex flex-col gap-4">
                <div class="flex flex-col gap-2">
                    <Label for="email">Email</Label>
                    <Input id="email" bind:value={email} placeholder="example@mail.com"/>
                </div>
                <div class="flex flex-col gap-2">
                    <Label for="password">Password</Label>
                    <Input id="password" bind:value={password} placeholder="********" type="password"/>
                </div>
            </div>
            {#if error}
                <div class="text-sm text-destructive" transition:fade>{error}</div>
            {/if}
            <div class="flex justify-between">
                <span class="text-sm self-end">Don't have an account? <a href="/auth/registration"
                                                                         class="text-primary underline-offset-4 hover:underline"> Sign up</a></span>
                <Button type="submit" class="w-fit" disabled={loginMutation.isLoading}>Sign in</Button>
            </div>
        </form>
    </CardContent>
</Card>