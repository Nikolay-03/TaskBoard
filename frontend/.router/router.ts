import { createRouter } from 'sv-router';
import Notfound from '../src/routes/[...notfound].svelte';
import Index from '../src/routes/index.svelte';
import Layout from '../src/routes/layout.svelte';

export const routes = {
  '*notfound': Notfound,
  '/': Index,
  'layout': Layout
};
export type Routes = typeof routes;
export const { p, navigate, isActive, preload, route } = createRouter(routes);
