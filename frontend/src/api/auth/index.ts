import { createMutation } from '@tanstack/svelte-query';
import { api } from '$api';
import type { ILoginBody, IRegisterBody, ILoginResult } from './auth';
import type { IUser } from '$api/user';

export * from './auth';

export const useLogin = () =>
	createMutation<ILoginResult, Error, ILoginBody>(() => ({
		mutationKey: ['login'],
		mutationFn: (body) => api.post('/auth/login', body)
	}));

export const useRegister = () =>
	createMutation<IUser, Error, IRegisterBody>(() => ({
		mutationKey: ['register'],
		mutationFn: (body) => api.post('/auth/register', body)
	}));

export const useLogout = () =>
	createMutation(() => ({
		mutationKey: ['logout'],
		mutationFn: () => api.post('/auth/logout')
	}));
