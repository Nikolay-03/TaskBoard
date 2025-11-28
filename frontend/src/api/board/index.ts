import {createQuery} from "@tanstack/svelte-query";
import type {IBoard} from "./board";
import {api} from "$api";
import type {IUser} from "$api/user";

export * from './board'
export const useBoard = (id: number) => createQuery(() => ({
    queryKey: ['board', id],
    queryFn: () => api.get<IBoard>(`/boards/${id}`),
    refetchOnMount: false,
}))

export const useMembers = (id: number) => createQuery(() => ({
    queryKey: ['members', id],
    queryFn: () => api.get<IUser[]>(`/boards/${id}/members`)
}))