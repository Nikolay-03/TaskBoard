import { Fetcher } from './fetcher';
import { QueryClient } from '@tanstack/svelte-query';

export const api = new Fetcher('http://localhost:8080/api');
export const queryClient = new QueryClient();
