import {createQuery} from "@tanstack/svelte-query";
import type {IBoard} from "./board";
import {api} from "$api/index";

export * from './board'
export const useBoard = (id: number) => createQuery(() => ({
    queryKey: ['board', id],
    queryFn: () => api.get<IBoard>(`/boards/${id}`)
}))