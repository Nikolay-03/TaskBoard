import {createQuery} from "@tanstack/svelte-query";
import type {IBoard, IBoardMember} from "./board";
import {api} from "$api/index";

export * from './board'
export const useBoard = (id: number) => createQuery(() => ({
    queryKey: ['board', id],
    queryFn: () => api.get<IBoard>(`/boards/${id}`)
}))

export const useMembers = (id: number) => createQuery(() => ({
    queryKey: ['members', id],
    queryFn: () => api.get<IBoardMember[]>(`/boards/${id}/members`)
}))