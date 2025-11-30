import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';
import {DateFormatter, parseAbsoluteToLocal} from "@internationalized/date";

export function cn(...inputs: ClassValue[]) {
	return twMerge(clsx(inputs));
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type WithoutChild<T> = T extends { child?: any } ? Omit<T, 'child'> : T;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type WithoutChildren<T> = T extends { children?: any } ? Omit<T, 'children'> : T;
export type WithoutChildrenOrChild<T> = WithoutChildren<WithoutChild<T>>;
export type WithElementRef<T, U extends HTMLElement = HTMLElement> = T & { ref?: U | null };

export const getRequestErrorMessage = (err: unknown) => {
	return err instanceof Error ? err.message : '';
};

export const formatUtcDate = (date: string) => {
	const formatter = new DateFormatter("ru-RU", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric"
	});

	return formatter.format(parseAbsoluteToLocal(date).toDate());
}