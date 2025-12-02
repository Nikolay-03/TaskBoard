import { z } from 'zod';

export const boardSchema = z.object({
	title: z.string().min(1, 'Title is required'),
	description: z.string().optional(),
	members: z.array(z.number()),
});
export type BoardSchema = z.infer<typeof boardSchema>;
