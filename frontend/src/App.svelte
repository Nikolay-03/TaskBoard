<script context="module">
    import {QueryClient, QueryClientProvider} from '@tanstack/svelte-query'
    import {Router} from 'sv-router';
    import 'sv-router/generated';

    const queryClient = new QueryClient()
</script>
<QueryClientProvider client={queryClient}>
    <Router/>
</QueryClientProvider>