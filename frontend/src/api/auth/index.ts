import { createMutation, createQuery } from '@tanstack/svelte-query';
import { api } from '$api';
import type { ILoginBody, IRegisterBody, ILoginResult } from './auth';
import type { IUser } from '$api/user';

export * from './auth';

export const useLogin = () =>
	createMutation(() => ({
		mutationKey: ['login'],
		mutationFn: (body: ILoginBody) => api.post<ILoginResult, ILoginBody>('/auth/login', body)
	}));

export const useRegister = () =>
	createMutation(() => ({
		mutationKey: ['register'],
		mutationFn: (body: IRegisterBody) => api.post<IUser, IRegisterBody>('/auth/register', body)
	}));
export const useUser = () =>
	createQuery(() => ({
		queryKey: ['me'],
		queryFn: () => api.get<IUser>('/auth/me'),
		retry: false
	}));
export const useLogout = () =>
	createMutation(() => ({
		mutationKey: ['logout'],
		mutationFn: () => api.post<unknown>('/auth/logout')
	}));
