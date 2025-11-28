import { createRouter } from 'sv-router';
import AuthLayout from '../src/routes/(auth)/layout.svelte';
import AuthLogin from '../src/routes/(auth)/login.svelte';
import AuthRegistration from '../src/routes/(auth)/registration.svelte';
import Notfound from '../src/routes/[...notfound].svelte';
import Index from '../src/routes/index.svelte';
import Layout from '../src/routes/layout.svelte';

export const routes = {
	'/(auth)': {
		layout: AuthLayout,
		'/login': AuthLogin,
		'/registration': AuthRegistration
	},
	'*notfound': Notfound,
	'/': Index,
	layout: Layout
};
export type Routes = typeof routes;
export const { p, navigate, isActive, preload, route } = createRouter(routes);
