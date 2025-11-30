import LayoutDashboard from '@lucide/svelte/icons/layout-dashboard';
import Star from '@lucide/svelte/icons/star';
import Settings from '@lucide/svelte/icons/settings';
import type { Component } from 'svelte';

export interface IMenuItem {
	id: string;
	name: string;
	icon: Component;
	href: string;
}

export const menuItems: IMenuItem[] = [
	{
		id: '1',
		name: 'Dashboard',
		href: '/',
		icon: LayoutDashboard
	},
	{
		id: '2',
		name: 'Favorites',
		href: '/favorites',
		icon: Star
	},
	{
		id: '3',
		name: 'Settings',
		href: '/settings',
		icon: Settings
	}
];
