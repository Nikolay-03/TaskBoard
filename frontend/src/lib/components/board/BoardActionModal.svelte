<script lang="ts">
    import {fade} from 'svelte/transition';
    import {DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle} from '$lib/ui/dialog';
    import type {Snippet} from 'svelte';
    import {Button, type ButtonProps} from '$lib/ui/button';
    import {InputField} from '$lib/components/form';
    import {toast} from 'svelte-sonner';
    import {queryClient} from '$api';
    import {type BoardSchema, boardSchema} from './schema';
    import {getRequestErrorMessage} from '$lib/utils';
    import {type IBoard, useCreateBoard, useDeleteBoard} from "$api/board";
    import {useUpdateBoard} from "$api/board/index.js";
    import {navigate} from "sv-router/generated";

    type ModeType = 'edit' | 'create';

    interface Props {
        trigger: Snippet<[ButtonProps]>;
        defaultValues?: IBoard;
        mode?: ModeType;
    }

    let open = $state(false);

    let {trigger, mode = 'create', defaultValues}: Props = $props();

    let title = $state<string>('');
    let description = $state<string | undefined>();

    const createBoardMutation = useCreateBoard();
    const updateBoardMutation = useUpdateBoard();
    const deleteBoardMutation = useDeleteBoard();

    let error = $state<string | null>(null);

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        const data: BoardSchema = {
            title,
            description
        };
        const result = boardSchema.safeParse(data);
        if (!result.success) {
            error = result.error.issues[0].message;
        } else {
            error = null;
            try {
                if (mode === 'create') {
                    const response = await createBoardMutation.mutateAsync({title, description});
                    navigate(`/boards/${response.id}`, { viewTransition: true })
                } else if (mode === 'edit' && defaultValues?.id) {
                    await updateBoardMutation.mutateAsync({body: {title, description}, id: defaultValues.id});
                    await queryClient.invalidateQueries({queryKey: ['board', defaultValues.id]});
                }
                onOpenChange(false);
            } catch (err) {
                const errorMessage = err instanceof Error ? err.message : '';
                toast.error(errorMessage);
            }
        }
    };
    const handleDeleteBoard = async (e: MouseEvent) => {
        e.stopPropagation();
        try {
            if (mode === 'edit' && defaultValues?.id) {
                await deleteBoardMutation.mutateAsync(defaultValues.id);
                navigate('/')
                await queryClient.invalidateQueries({queryKey: ['boards']});
            }
        } catch (err) {
            const errorMessage = getRequestErrorMessage(err);
            toast.error(errorMessage);
        }
    };
    const onOpenChange = (openVal: boolean) => {
        open = openVal;
    };
    $effect(() => {
        if (open) {
            title = defaultValues?.title ?? '';
            description = defaultValues?.description
        }
    });
</script>

<Dialog {open} {onOpenChange}>
    <DialogTrigger onclick={(e) => e.stopPropagation()}>
        {#snippet child({props})}
            {@render trigger(props)}
        {/snippet}
    </DialogTrigger>
    <DialogContent class="max-w-[512px]">
        <DialogHeader>
            <DialogTitle>{mode === 'create' ? 'Create board' : 'Edit board'}</DialogTitle>
        </DialogHeader>
        <form onsubmit={handleSubmit} class="flex flex-col gap-3">
            <InputField title="Title" bind:value={title} placeholder="Task title"/>
            <InputField title="Description" bind:value={description} placeholder="Task description"/>
            {#if error}
                <div class="text-sm text-destructive" transition:fade>{error}</div>
            {/if}
            <div class="flex justify-between">
                {#if mode === 'edit'}
                    <Button variant="destructive" onclick={handleDeleteBoard}>Delete</Button>
                {/if}
                <Button
                        type="submit"
                        class="ml-auto"
                        disabled={createBoardMutation.isPending || updateBoardMutation.isPending}
                >
                    {mode === 'create' ? 'Create' : 'Edit'}
                </Button>
            </div>
        </form>
    </DialogContent>
</Dialog>
