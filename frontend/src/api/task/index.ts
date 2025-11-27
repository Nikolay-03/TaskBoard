import {createMutation} from "@tanstack/svelte-query";
import type {ICreateTaskBody, ICreateTaskVariables, ITask} from "./task";
import {api} from "$api";

export * from './task'

export const useCreateTask = () => createMutation<ITask, Error, ICreateTaskVariables>(() => ({
    mutationKey: ['createTask'],
    mutationFn: ({columnId, body}) => api.post<ITask, ICreateTaskBody>(`/tasks/${columnId}`, body)
}))