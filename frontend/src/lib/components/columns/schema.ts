import { z } from 'zod';

export const createColumnSchema = z.object({
	title: z.string().min(1, 'Title is required')
});
export type CreateColumnSchema = z.infer<typeof createColumnSchema>;
