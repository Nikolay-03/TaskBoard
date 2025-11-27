<script lang="ts">
    import {DialogTrigger, Dialog, DialogContent, DialogHeader, DialogTitle} from "$lib/ui/dialog";
    import type {Snippet} from "svelte";
    import {Button, type ButtonProps} from "$lib/ui/button";
    import {useLabels} from "$api/label";
    import {useMembers} from "$api/board";
    import AssigneesField from "./AssigneesField.svelte";
    import ParticipantsField from "./ParticipantsField.svelte";
    import LabelsField from "./LabelsField.svelte";
    import {InputField} from "$lib/components/form";

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
    let dueDate = $state<string>('');
    let labels = $state<number[]>([]);
    let participants = $state<number[]>([]);
    let assignees = $state<number[]>([]);
    let {trigger, columnId, boardId}: Props = $props();
    const membersQuery = useMembers(boardId)
    const labelsQuery = useLabels()
    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault()
        console.log($state.snapshot(labels))
        console.log($state.snapshot(participants))
        console.log($state.snapshot(assignees))

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
            <InputField title="Title" value={title} placeholder="Task Title"/>
            <InputField title="Description" value={description} placeholder="TaskDescription"/>
            <InputField title="Deadline date" value={dueDate} placeholder="Task deadline date" type="date"/>
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
            <Button type="submit" class="w-fit ml-auto">
                Create
            </Button>
        </form>
    </DialogContent>
</Dialog>