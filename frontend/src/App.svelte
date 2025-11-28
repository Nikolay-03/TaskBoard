<script context="module">
    import {QueryClientProvider} from '@tanstack/svelte-query'
    import {SvelteQueryDevtools} from "@tanstack/svelte-query-devtools";
    import {Router} from 'sv-router';
    import {queryClient} from "$api";
    import 'sv-router/generated';

</script>
<QueryClientProvider client={queryClient}>
    <Router/>
    <SvelteQueryDevtools/>
</QueryClientProvider>