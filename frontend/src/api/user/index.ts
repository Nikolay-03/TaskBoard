import {createMutation, createQuery} from "@tanstack/svelte-query";
import {api} from "$api";
import type {IUpdateUserBody, IUser} from "./user";

export * from './user';

export const useUser = () =>
    createQuery<void, Error, IUser>(() => ({
        queryKey: ['me'],
        queryFn: () => api.get('/me'),
        retry: false
    }));
export const useUpdateUser = () => createMutation<IUser, Error, IUpdateUserBody>(() => ({
    mutationKey: ['updateUser'],
    mutationFn: (body) => api.patch('/me', body),
}))