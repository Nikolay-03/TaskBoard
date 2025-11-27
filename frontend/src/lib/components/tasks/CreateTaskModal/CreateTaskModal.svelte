<script lang="ts">
    import {fade} from 'svelte/transition';
    import {DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle} from "$lib/ui/dialog";
    import type {Snippet} from "svelte";
    import {Button, type ButtonProps} from "$lib/ui/button";
    import {useLabels} from "$api/label";
    import {useMembers} from "$api/board";
    import AssigneesField from "./AssigneesField.svelte";
    import ParticipantsField from "./ParticipantsField.svelte";
    import LabelsField from "./LabelsField.svelte";
    import {DateField, InputField, TextAreaField} from "$lib/components/form";
    import {type CalendarDate, getLocalTimeZone, today} from "@internationalized/date";
    import {toast} from "svelte-sonner";
    import {createTaskSchema, type CreateTaskSchema} from "$lib/components/tasks/CreateTaskModal/schema";
    import {useCreateTask} from "$api/task";
    import {queryClient} from "$api";

    interface Props {
        trigger: Snippet<[ButtonProps]>,
        columnId: number
        boardId: number
    }
    let labelsModalOpen = $state<boolean>(false)
    let participantsModalOpen = $state<boolean>(false)
    let assigneesModalOpen = $state<boolean>(false)

    let title = $state<string>('');
    let description = $state<string>('');
    let dueDate = $state<CalendarDate>(today(getLocalTimeZone()))
    let labels = $state<number[]>([]);
    let participants = $state<number[]>([]);
    let assignees = $state<number[]>([]);

    let {trigger, columnId, boardId}: Props = $props();

    const createTaskMutation = useCreateTask()
    const membersQuery = useMembers(boardId)
    const labelsQuery = useLabels()

    let error = $state<string | null>(null)

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault()
        const data: CreateTaskSchema = {title, description, dueDate: dueDate.toString(), labels, participants, assignees}
        const result = createTaskSchema.safeParse(data);
        if (!result.success) {
            error = result.error.issues[0].message
        } else {
            error = null
            try {
                await createTaskMutation.mutateAsync({columnId, body:data});
                await queryClient.invalidateQueries({queryKey: ['board', boardId]})
            } catch (err) {
                const errorMessage = err instanceof Error ? err.message : "";
                toast.error(errorMessage)
            }
        }
    }
    const handleLabelModalOpenChange = (open: boolean) => {
        labelsModalOpen = open;
    };
    const handleAssigneesModalOpenChange = (open: boolean) => {
        assigneesModalOpen = open;
    };
    const handleParticipantsModalOpenChange = (open: boolean) => {
        participantsModalOpen = open;
    };

</script>
<Dialog>
    <DialogTrigger>
        {#snippet child({props})}
            {@render trigger(props)}
        {/snippet}
    </DialogTrigger>
    <DialogContent class="min-h-[400px] max-w-[512px]">
        <DialogHeader>
            <DialogTitle>Create task</DialogTitle>
        </DialogHeader>
        <form onsubmit={handleSubmit} class="flex flex-col gap-3">
            <InputField title="Title" bind:value={title} placeholder="Task Title"/>
            <TextAreaField title="Description" bind:value={description} placeholder="TaskDescription"/>
            <DateField title="Deadline date" bind:value={dueDate}/>
            <Button onclick={() => labelsModalOpen = true} variant="outline">
                Select labels
            </Button>
           <LabelsField
                   open={labelsModalOpen}
                   handleOpenChange={handleLabelModalOpenChange}
                   labelsOpts={labelsQuery.data}
                   {labels}
           />
            <Button onclick={() => assigneesModalOpen = true} variant="outline">
                Select assignees
            </Button>
            <AssigneesField
                    open={assigneesModalOpen}
                    handleOpenChange={handleAssigneesModalOpenChange}
                    members={membersQuery.data}
                    {assignees}
            />
            <Button onclick={() => participantsModalOpen = true} variant="outline">
                Select participants
            </Button>
            <ParticipantsField
                    open={participantsModalOpen}
                    handleOpenChange={handleParticipantsModalOpenChange}
                    members={membersQuery.data}
                    {participants}
            />
            <div class="flex justify-between">
                {#if error}
                    <div class="text-sm text-destructive" transition:fade>{error}</div>
                {/if}
                <Button type="submit" class="ml-auto" disabled={createTaskMutation.isPending}>
                    Create
                </Button>
            </div>
        </form>
    </DialogContent>
</Dialog>