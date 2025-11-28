import { z } from 'zod';

export const LoginSchema = z.object({
	email: z.email(),
	password: z.string().min(1, { error: 'Enter password' })
});

export const RegistrationSchema = z.object({
	name: z.string().min(1, { error: 'Enter your name' }),
	email: z.email(),
	password: z.string().min(1, { error: 'Enter password' })
});
