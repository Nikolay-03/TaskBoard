import {createMutation, createQuery} from '@tanstack/svelte-query';
import type {
    IBoard,
    IBoardView,
    ICreateBoardBody,
    IUpdateBoardVariables,
    OptimisticContext
} from './board';
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
export const useGetFavorites = () => createQuery<void, Error, IBoard[]>(() => ({
    queryKey: ['favorites'],
    queryFn: () => api.get('/boards/favorites')
}))
export const useAddBoardToFavorites = () => createMutation<void, Error, number, OptimisticContext>(() => ({
    mutationKey: ['addBoardToFavorites'],
    mutationFn: (id) => api.post(`/boards/${id}/favorite`),
    onMutate: optimisticFavoritesAction,
    onError: (err, id, context) => {
        if (context?.previousBoards) {
            queryClient.setQueryData(['boards'], context.previousBoards);
        }
        if (context?.previousBoard) {
            queryClient.setQueryData(['board', id], context.previousBoard);
        }
        if (context?.previousFavorites) {
            queryClient.setQueryData(['favorites'], context.previousFavorites);
        }
    },
    onSettled: (_data, _error, id) => {
        queryClient.invalidateQueries({ queryKey: ['boards'] });
        queryClient.invalidateQueries({ queryKey: ['board', id] });
        queryClient.invalidateQueries({ queryKey: ['favorites'] });
    },
}))
export const useDeleteBoardFromFavorites = () => createMutation<void, Error, number, OptimisticContext>(() => ({
    mutationKey: ['deleteBoardFromFavorites'],
    mutationFn: (id) => api.delete(`/boards/${id}/favorite`),
    onMutate: optimisticFavoritesAction,
    onError: (err, id, context) => {
        if (context?.previousBoards) {
            queryClient.setQueryData(['boards'], context.previousBoards);
        }
        if (context?.previousBoard) {
            queryClient.setQueryData(['board', id], context.previousBoard);
        }
        if (context?.previousFavorites) {
            queryClient.setQueryData(['favorites'], context.previousFavorites);
        }
    },
    onSettled: (_data, _error, id) => {
        queryClient.invalidateQueries({ queryKey: ['boards'] });
        queryClient.invalidateQueries({ queryKey: ['board', id] });
        queryClient.invalidateQueries({ queryKey: ['favorites'] });
    },
}))

const optimisticFavoritesAction = async (id: number) => {
    await queryClient.cancelQueries({ queryKey: ['boards'] })
    await queryClient.cancelQueries({ queryKey: ['board', id] })
    await queryClient.cancelQueries({queryKey: ['favorites']})

    const previousBoards = queryClient.getQueryData<IBoard[]>(['boards']);
    const previousBoard = queryClient.getQueryData<IBoardView>(['board', id]);
    const previousFavorites = queryClient.getQueryData<IBoard[]>(['favorites'])

    const updatedBoards = previousBoards?.map(board =>
        board.id === id ? { ...board, isFavorite: !board.isFavorite } : board
    );

    const updatedBoard = previousBoard
        ? { ...previousBoard, isFavorite: !previousBoard.isFavorite }
        : undefined;

    const updatedFavorites = previousFavorites?.filter(board => board.id !== id);

    if (updatedBoards) {
        queryClient.setQueryData(['boards'], updatedBoards);
    }

    if (updatedBoard) {
        queryClient.setQueryData(['board', id], updatedBoard);
    }
    if (updatedFavorites) {
        queryClient.setQueryData(['favorites'], updatedFavorites);
    }

    return { previousBoards, previousBoard, previousFavorites } satisfies OptimisticContext;
};
