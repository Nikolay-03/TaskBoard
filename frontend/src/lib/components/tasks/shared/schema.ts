import { z } from 'zod';

export const createTaskSchema = z.object({
	title: z.string().min(1, 'Title is required'),
	description: z.string(),
	dueDate: z.string(),
	labels: z.array(z.number()),
	participants: z.array(z.number()),
	assignees: z.array(z.number())
});
export type CreateTaskSchema = z.infer<typeof createTaskSchema>;
