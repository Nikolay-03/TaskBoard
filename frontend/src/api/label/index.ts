import {createQuery} from "@tanstack/svelte-query";
import {api} from "$api";
import type {ILabel} from "$api/labels";

export * from './label';
export const useLabels = () => createQuery(() => ({
    queryKey: ['labels'],
    queryFn: () => api.get<ILabel[]>('/labels')
}))