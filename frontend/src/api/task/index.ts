import { createMutation, createQuery } from '@tanstack/svelte-query';
import type { ICreateTaskVariables, IUpdateTaskVariables, ITask } from './task';
import { api } from '$api';

export * from './task';

export const useCreateTask = () =>
	createMutation<ITask, Error, ICreateTaskVariables>(() => ({
		mutationKey: ['createTask'],
		mutationFn: ({ columnId, body }) => api.post(`/tasks/${columnId}`, body)
	}));
export const useUpdateTask = () =>
	createMutation<ITask, Error, IUpdateTaskVariables>(() => ({
		mutationKey: ['updateTask'],
		mutationFn: ({ id, body }) => api.patch(`/tasks/${id}`, body)
	}));
export const useGetTask = (id: number) =>
	createQuery<ITask>(() => ({
		queryKey: ['task', id],
		queryFn: () => api.get(`/tasks/${id}`),
		enabled: false
	}));
export const useDeleteTask = () =>
	createMutation<void, Error, number>(() => ({
		mutationKey: ['deleteTask'],
		mutationFn: (id) => api.delete(`/tasks/${id}`)
	}));
