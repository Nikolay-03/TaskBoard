import { createQuery } from '@tanstack/svelte-query';
import type { IBoard } from './board';
import { api } from '$api';
import type { IUser } from '$api/user';

export * from './board';
export const useBoard = (id: number) =>
	createQuery<IBoard>(() => ({
		queryKey: ['board', id],
		queryFn: () => api.get(`/boards/${id}`),
		refetchOnMount: false,
		refetchOnWindowFocus: false,
	}));

export const useMembers = (id: number) =>
	createQuery<IUser[]>(() => ({
		queryKey: ['members', id],
		queryFn: () => api.get(`/boards/${id}/members`)
	}));
