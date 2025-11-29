import { createMutation } from '@tanstack/svelte-query';
import { api } from '$api';
import type { IColumn, ICreateColumnBody, IUpdateColumnVariables } from '$api/column/column';

export * from './column';
export const useCreateColumn = (boardId: number) =>
	createMutation<IColumn, Error, ICreateColumnBody>(() => ({
		mutationKey: ['createColumn', boardId],
		mutationFn: (body) => api.post(`/columns/${boardId}`, body)
	}));
export const useUpdateColumn = () =>
	createMutation<IColumn, Error, IUpdateColumnVariables>(() => ({
		mutationKey: ['updateColumn'],
		mutationFn: ({ id, body }) => api.patch(`/columns/${id}`, body)
	}));
export const useDeleteColumn = () =>
	createMutation<void, Error, number>(() => ({
		mutationKey: ['deleteColumn'],
		mutationFn: (id) => api.delete(`/columns/${id}`)
	}));
