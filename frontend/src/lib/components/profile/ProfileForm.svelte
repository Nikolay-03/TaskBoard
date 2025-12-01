<script lang="ts">
    import {fade} from 'svelte/transition'
    import {Card} from "$lib/ui/card";
    import {CardContent, CardHeader, CardTitle} from "$lib/ui/card/index.js";
    import {UserAvatar} from "$lib/components";
    import {InputField} from "$lib/components/form";
    import {Button} from "$lib/ui/button";
    import {useUpdateUser, useUser} from "$api/user";
    import {toast} from "svelte-sonner";
    import {type UpdateProfileSchema, updateProfileSchema} from "$lib/components/profile/schema";
    import {queryClient} from "$api";


    const user = useUser()
    const userUpdate = useUpdateUser()

    let name = $state<string>(user.data?.name ?? '')
    let avatar = $state<string>(user.data?.avatar ?? '')
    let error = $state<string | null>(null)

    let isChanged = $state<boolean>(false)
    const onReset = () => {
        name = user.data?.name ?? ''
        avatar = user.data?.avatar ?? ''
    }
    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        const data: UpdateProfileSchema = {
            name,
            avatar
        };
        const result = updateProfileSchema.safeParse(data);
        if (!result.success) {
            error = result.error.issues[0].message;
        } else {
            error = null;
            try {
                await userUpdate.mutateAsync({name, avatar})
                await queryClient.invalidateQueries({queryKey: ['me']})
            } catch (err) {
                const errorMessage = err instanceof Error ? err.message : '';
                toast.error(errorMessage);
            }
        }
    };
    $effect(() => {
        if(user.data){
            isChanged = user.data.name !== name || user.data.avatar !== avatar
        }
    })
</script>

<Card class="w-full h-full max-w-[500px] max-h-[700px] gap-10">
    <CardHeader>
        <CardTitle class="text-center text-2xl">Profile</CardTitle>
    </CardHeader>
    <CardContent class="flex flex-col flex-1">
        {#if user.isLoading}
            <UserAvatar className="size-40 mx-auto"/>
            {:else if user.data}
            <form onsubmit={handleSubmit} class="flex flex-col gap-10 flex-1">
                <UserAvatar className="size-40 mx-auto" {avatar}/>
                <InputField title="Change name" placeholder="Your name" bind:value={name}/>
                <InputField title="Change avatar" placeholder="Paste link" bind:value={avatar}/>
                {#if error}
                    <div class="text-sm text-destructive" transition:fade>{error}</div>
                {/if}
                <div class="flex items-center mt-auto">
                    {#if isChanged}
                        <div transition:fade={{duration: 200}}>
                            <Button class="w-fit" onclick={onReset}>
                                Reset
                            </Button>
                        </div>
                    {/if}
                    <Button class="w-fit ml-auto" type="submit" disabled={!isChanged}>
                        Save
                    </Button>
                </div>
            </form>
        {/if}
    </CardContent>
</Card>