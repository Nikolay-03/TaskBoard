import { createQuery } from '@tanstack/svelte-query';
import { api } from '$api';
import type { ILabel } from '$api/labels';

export * from './label';
export const useLabels = () =>
	createQuery<ILabel[]>(() => ({
		queryKey: ['labels'],
		queryFn: () => api.get('/labels')
	}));
