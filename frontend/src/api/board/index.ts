import {createMutation, createQuery} from '@tanstack/svelte-query';
import type {IBoard, IBoardView, ICreateBoardBody, IUpdateBoardVariables} from './board';
import {api, queryClient} from '$api';
import type {IUser} from '$api/user';

export * from './board';
export const useGetBoards = () => createQuery<void, Error, IBoard[]>(() => ({
    queryKey: ['boards'],
    queryFn: () => api.get('/boards'),
}))
export const useBoard = (id: number) =>
    createQuery<IBoardView>(() => ({
        queryKey: ['board', id],
        queryFn: () => api.get(`/boards/${id}`),
        refetchOnMount: false,
        refetchOnWindowFocus: false,
        enabled: Boolean(id),
    }));

export const useMembers = (id: number) =>
    createQuery<IUser[]>(() => ({
        queryKey: ['members', id],
        queryFn: () => api.get(`/boards/${id}/members`)
    }));
export const useCreateBoard = () => createMutation<IBoard, Error, ICreateBoardBody>(() => ({
    mutationKey: ['createBoard'],
    mutationFn: (body) => api.post(`/boards`, body),
}))
export const useUpdateBoard = () =>
    createMutation<IBoard, Error, IUpdateBoardVariables>(() => ({
        mutationKey: ['updateBoard'],
        mutationFn: ({id, body}) => api.patch(`/boards/${id}`, body)
    }));
export const useDeleteBoard = () =>
    createMutation<IBoard, Error, number>(() => ({
        mutationKey: ['deleteBoard'],
        mutationFn: (id) => api.delete(`/boards/${id}`)
    }));

export const useAddBoardToFavorites = () => createMutation<void, Error, number>(() => ({
    mutationKey: ['addBoardToFavorite'],
    mutationFn: (id) => api.post(`/boards/${id}/favorite`),
    onMutate: optimisticFavoritesAction,
}))
export const useDeleteBoardFromFavorites = () => createMutation<void, Error, number>(() => ({
    mutationKey: ['addBoardToFavorite'],
    mutationFn: (id) => api.delete(`/boards/${id}/favorite`),
    onMutate: optimisticFavoritesAction,
}))

const optimisticFavoritesAction = async (id: number) => {
    await queryClient.cancelQueries({ queryKey: ['boards'] })
    const previousBoards: IBoard[] | undefined = queryClient.getQueryData(['boards'])
    const updatedBoards = previousBoards?.map(board => {
        if (board.id === id) {
            return {...board, isFavorite: !board.isFavorite};
        }
        return board;
    })
    queryClient.setQueryData(['boards'], () => updatedBoards)

    return { previousBoards }
}