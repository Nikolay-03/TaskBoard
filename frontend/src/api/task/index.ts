import { createMutation, createQuery } from '@tanstack/svelte-query';
import type { ICreateTaskBody, ICreateTaskVariables, ITask } from './task';
import { api } from '$api';

export * from './task';

export const useCreateTask = () =>
	createMutation<ITask, Error, ICreateTaskVariables>(() => ({
		mutationKey: ['createTask'],
		mutationFn: ({ columnId, body }) => api.post<ITask, ICreateTaskBody>(`/tasks/${columnId}`, body)
	}));

export const useGetTask = (id: number) =>
	createQuery(() => ({
		queryKey: ['task', id],
		queryFn: () => api.get<ITask>(`/tasks/${id}`),
		enabled: false
	}));
export const useDeleteTask = (id: number) => createMutation(() => ({
	mutationKey: ['task', id],
	mutationFn: () => api.delete(`/tasks/${id}`),
}))