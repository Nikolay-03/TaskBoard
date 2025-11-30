import { createRouter } from 'sv-router';
import AuthLayout from '../src/routes/(auth)/layout.svelte';
import AuthLogin from '../src/routes/(auth)/login.svelte';
import AuthRegistration from '../src/routes/(auth)/registration.svelte';
import Notfound from '../src/routes/[...notfound].svelte';
import BoardsId from '../src/routes/boards/[id].svelte';
import Index from '../src/routes/index.svelte';
import Layout from '../src/routes/layout.svelte';

export const routes = {
  '/(auth)': {
    'layout': AuthLayout,
    '/login': AuthLogin,
    '/registration': AuthRegistration
  },
  '*notfound': Notfound,
  '/boards': {
    '/:id': BoardsId
  },
  '/': Index,
  'layout': Layout
};
export type Routes = typeof routes;
export const { p, navigate, isActive, preload, route } = createRouter(routes);
