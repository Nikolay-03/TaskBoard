import { z } from 'zod';

export const updateProfileSchema = z.object({
    name: z.string().min(2, 'Name must have at least 2 characters'),
    avatar: z.url().optional(),
});
export type UpdateProfileSchema = z.infer<typeof updateProfileSchema>;
